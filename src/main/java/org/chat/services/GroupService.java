package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.User;
import org.chat.exceptions.*;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.UserRepository;

import java.util.*;

@ApplicationScoped
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final UserRepository userRepository;

    @Transactional
    public Group createGroup(Group group, String[] creators, String userId) {
        if (group == null || group.getName() == null || group.getName().isEmpty()) {
            throw new InvalidGroupException("Invalid group name");
        }

        if (creators == null) {
            creators = new String[]{};
        }

        if (groupRepository.find("name", group.getName()).firstResult() != null) {
            throw new InvalidGroupException("Group already exists");
        }

        group.setId(UUID.randomUUID().toString());
        groupRepository.persist(group);
        User currentUser = userRepository.findById(userId);

        List<GroupUser> creatorsList = new ArrayList<>(
            Arrays.stream(creators)
                .map(userRepository::findById)
                .filter(creator -> !creator.getId().equals(userId))
                .map(creator -> new GroupUser(UUID.randomUUID().toString(), group, creator, true, true))
                .toList()
        );

        creatorsList.add(new GroupUser(UUID.randomUUID().toString(), group, currentUser, true, true));

        groupUserRepository.persist(creatorsList);

        return group;
    }

    @Transactional
    public GroupUser joinGroup(String groupId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));

        GroupUser groupUser = null;
        try {
            groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);
        }
        catch (ResourceNotFoundException e) {}

        if (groupUser != null) {
            throw new UnableToJoinGroupException("you're already a member of this group or have submitted a request to join group");
        }

        User user = userRepository.findById(userId);

        groupUser = new GroupUser(UUID.randomUUID().toString(), group, user, false, false);

        groupUserRepository.persist(groupUser);

        return groupUser;
    }

    @Transactional
    public String leaveGroup(String groupId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));

        groupUserRepository.delete(groupUserRepository.findByGroupIdUserId(groupId, userId));
        return "you left the group";
    }

    @Transactional
    public GroupUser acceptToGroup(String groupId, String creatorId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));
        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException("You do not have permission to accept join requests in this group");
        }

        User recipient = userRepository.findById(userId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);

        if (groupUser.getIsMember()) {
            throw new UnableToJoinGroupException("this user is already a member");
        }

        groupUser.setIsCreator(false);
        groupUser.setIsMember(true);

        groupUserRepository.getEntityManager().merge(groupUser);

        return groupUser;
    }

    @Transactional
    public String rejectFromEnteringGroup(String groupId, String creatorId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));

        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException("You do not have permission to accept join requests in this group");
        }

        User user = userRepository.findById(userId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId);
        groupUserRepository.delete(groupUser);

        return "user has been rejected";
    }

    public List<GroupUser> getWaitingUsers(String groupId, String creatorId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));
        GroupUser creator =
                groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException("you do not have permission to check for waiting user");
        }

        return groupUserRepository
                .getWaitingUsers(group.getId());
    }

    public List<Group> getUserJoinedGroups(String userId) {
        return groupUserRepository.getUserGroups(userId)
                .stream()
                .map(GroupUser::getGroup)
                .toList();
    }

    public List<Group> getGroups(String groupName) {
        return groupRepository.getGroups(groupName);
    }
}
