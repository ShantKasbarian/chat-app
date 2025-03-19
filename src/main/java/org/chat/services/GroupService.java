package org.chat.services;

import io.quarkus.runtime.Startup;
import jakarta.transaction.Transactional;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.User;
import org.chat.exceptions.*;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.UserRepository;

import java.util.*;

@Startup
public class GroupService {
    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final UserRepository userRepository;

    public GroupService(
            GroupRepository groupRepository,
            GroupUserRepository groupUserRepository,
            UserRepository userRepository
    ) {
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.userRepository = userRepository;

    }

    @Transactional
    public Group createGroup(final Group group, String[] creators, String userId) {
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
                .map(userRepository::findByUsername)
                .filter(creator -> !creator.getId().equals(userId))
                .map(creator -> new GroupUser(UUID.randomUUID().toString(), group, creator, true, true))
                .toList()
        );

        creatorsList.add(new GroupUser(UUID.randomUUID().toString(), group, currentUser, true, true));

        groupUserRepository.persist(creatorsList);

        return group;
    }

    @Transactional
    public String joinGroup(String groupName, String userId) {
        Group group = groupRepository.findByName(groupName);

        GroupUser groupUser = null;
        try {
            groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);
        }
        catch (ResourceNotFoundException e) {}

        if (groupUser != null) {
            throw new UnableToJoinGroupException("you're already a member of this group or have submitted a request to join group");
        }

        User user = userRepository.findById(userId);
        groupUserRepository.persist(new GroupUser(UUID.randomUUID().toString(), group, user, false, false));

        return "request to join group has been submitted, waiting for one of the group creators to accept";
    }

    @Transactional
    public String leaveGroup(String groupName, String userId) {
        Group group = groupRepository.findByName(groupName);

        groupUserRepository.delete(groupUserRepository.findByGroupIdUserId(group.getId(), userId));
        return "you left the group";
    }

    @Transactional
    public String acceptToGroup(String groupName, String creatorId, String userName) {
        Group group = groupRepository.findByName(groupName);
        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException("You do not have permission to accept join requests in this group");
        }

        User recipient = userRepository.findByUsername(userName);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), recipient.getId());

        if (groupUser.getIsMember()) {
            throw new UnableToJoinGroupException("this user is already a member");
        }

        groupUser.setIsCreator(false);
        groupUser.setIsMember(true);

        groupUserRepository.getEntityManager().merge(groupUser);

        return "user has been accepted";
    }

    @Transactional
    public String rejectFromEnteringGroup(String groupName, String creatorId, String userName) {
        Group group = groupRepository.findByName(groupName);
        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException("You do not have permission to accept join requests in this group");
        }

        User user = userRepository.findByUsername(userName);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), user.getId());
        groupUserRepository.delete(groupUser);

        return "user has been rejected";
    }

    public List<String> getWaitingUsers(String groupName, String creatorId) {
        Group group = groupRepository.findByName(groupName);
        GroupUser creator =
                groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException("you do not have permission to check for waiting user");
        }

        return groupUserRepository
                .getWaitingUsers(group.getId())
                .stream()
                .map(groupUser ->  groupUser.getUser().getUsername())
                .toList();
    }

    public List<String> getUserJoinedGroups(String userId) {
        return groupUserRepository.getUserGroups(userId)
                .stream()
                .map(groupUser -> groupUser.getGroup().getName())
                .toList();
    }

    public List<String> getGroups(String groupName) {
        return groupRepository.getGroups(groupName)
                .stream()
                .map(Group::getName)
                .toList();
    }
}
