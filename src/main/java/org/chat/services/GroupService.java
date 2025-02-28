package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.User;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.UserRepository;

import java.util.*;

@ApplicationScoped
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
    public String createGroup(Group group, String[] creators, String userId) {
        if (group.getName() == null || group.getName().isEmpty()) {
            throw new RuntimeException("Invalid group name");
        }

        Group g = groupRepository.findByName(group.getName());

        if (g != null) {
            throw new RuntimeException("Group already exists");
        }

        groupRepository.persist(group);

        final Group gr = groupRepository.findByName(group.getName());
        User currentUser = userRepository.findById(Long.valueOf(userId));

        List<GroupUser> creatorsList = new ArrayList<>(
            Arrays.stream(creators)
                .map(userRepository::findByUsername)
                .filter(creator -> creator != null)
                .map(creator -> new GroupUser(gr, creator.getId(), true, true))
                .toList()
        );

        GroupUser currentGroupUser = new GroupUser(gr, currentUser.getId(), true, true);

        if (!creatorsList.contains(currentGroupUser)) {
            creatorsList.add(currentGroupUser);
        }

        groupUserRepository.persist(creatorsList);

        return "group has been created";
    }

    @Transactional
    public String joinGroup(String groupName, int userId) {
        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new RuntimeException("Group does not exist");
        }

        groupUserRepository.persist(new GroupUser(group, userId, false, false));

        return "request to join group has been submitted, waiting for one of the group creators to accept";
    }

    @Transactional
    public String leaveGroup(String groupName, int userId) {
        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new RuntimeException("Group does not exist");
        }

        groupUserRepository.delete(groupUserRepository.findByGroupIdUserId(group.getId(), userId));

        return "you left the group";
    }

    @Transactional
    public String acceptToGroup(String groupName, int creatorId, String userName) {
        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new RuntimeException("Group does not exist");
        }

        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new RuntimeException("You do not have permission to accept join requests in this group");
        }

        User user = userRepository.findByUsername(userName);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), user.getId());

        if (groupUser == null) {
            throw new RuntimeException("user not found");
        }

        groupUser.setIsCreator(false);
        groupUser.setIsMember(true);

        groupUserRepository.getEntityManager().merge(groupUser);

        return "user has been accepted";
    }

    @Transactional
    public String rejectFromEnteringGroup(String groupName, int creatorId, String userName) {
        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new RuntimeException("Group does not exist");
        }

        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new RuntimeException("You do not have permission to accept join requests in this group");
        }

        User user = userRepository.findByUsername(userName);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), user.getId());

        if (groupUser == null) {
            throw new RuntimeException("user not found");
        }

        groupUserRepository.delete(groupUser);

        return "user has been rejected";
    }

    public List<String> getWaitingUsers(String groupName, int creatorId) {
        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new RuntimeException("Group does not exist");
        }

        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (creator == null) {
            throw new RuntimeException("user not found");
        }

        return groupUserRepository.getWaitingUsers(group.getId());
    }

    public List<String> getUserJoinedGroups(int userId) {
        return groupUserRepository.getUserGroups(userId);
    }

    public List<String> getGroups(String groupName) {
        return groupRepository.getGroups(groupName);
    }
}
