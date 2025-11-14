package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.exception.*;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.UserRepository;
import org.chat.service.GroupService;

import java.util.*;

@ApplicationScoped
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final String INVALID_GROUP_NAME_MESSAGE = "Invalid group name";

    private static final String GROUP_ALREADY_EXISTS_MESSAGE = "Group already exists";

    private static final String GROUP_NOT_FOUND_MESSAGE = "group not found";

    private static final String ALREADY_MEMBER_OF_GROUP_MESSAGE = "you're already a member of this group or have submitted a request to join group";

    private static final String SUCCESSFUL_LEAVE_GROUP_MESSAGE = "you left the group";

    private static final String REQUEST_NOT_AUTHORIZED = "You do not have the necessary permissions to perform this request";

    private static final String USER_REJECTION_MESSAGE = "user has been rejected";

    private static final String TARGET_USER_ALREADY_MEMBER_OF_GROUP = "this user is already a member";

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Group createGroup(Group group, String[] creators, String userId) {
        if (group == null || group.getName() == null || group.getName().isEmpty()) {
            throw new InvalidGroupException(INVALID_GROUP_NAME_MESSAGE);
        }

        if (creators == null) {
            creators = new String[]{};
        }

        if (groupRepository.findByName(group.getName()).isPresent()) {
            throw new InvalidGroupException(GROUP_ALREADY_EXISTS_MESSAGE);
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

    @Override
    @Transactional
    public GroupUser joinGroup(String groupId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));

        GroupUser groupUser = null;
        try {
            groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);
        }
        catch (ResourceNotFoundException e) {}

        if (groupUser != null) {
            throw new UnableToJoinGroupException(ALREADY_MEMBER_OF_GROUP_MESSAGE);
        }

        User user = userRepository.findById(userId);

        groupUser = new GroupUser(UUID.randomUUID().toString(), group, user, false, false);

        groupUserRepository.persist(groupUser);

        return groupUser;
    }

    @Override
    @Transactional
    public String leaveGroup(String groupId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));

        groupUserRepository.delete(groupUserRepository.findByGroupIdUserId(groupId, userId));
        return SUCCESSFUL_LEAVE_GROUP_MESSAGE;
    }

    @Override
    @Transactional
    public GroupUser acceptToGroup(String groupId, String creatorId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));
        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        User recipient = userRepository.findById(userId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);

        if (groupUser.getIsMember()) {
            throw new UnableToJoinGroupException(TARGET_USER_ALREADY_MEMBER_OF_GROUP);
        }

        groupUser.setIsCreator(false);
        groupUser.setIsMember(true);

        groupUserRepository.getEntityManager().merge(groupUser);

        return groupUser;
    }

    @Override
    @Transactional
    public String rejectFromEnteringGroup(String groupId, String creatorId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));

        GroupUser creator = groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        User user = userRepository.findById(userId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId);
        groupUserRepository.delete(groupUser);

        return USER_REJECTION_MESSAGE;
    }

    @Override
    public List<GroupUser> getWaitingUsers(String groupId, String creatorId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));
        GroupUser creator =
                groupUserRepository.findByGroupIdUserId(group.getId(), creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        return groupUserRepository
                .getWaitingUsers(group.getId());
    }

    @Override
    public List<Group> getUserJoinedGroups(String userId) {
        return groupUserRepository.getUserGroups(userId)
                .stream()
                .map(GroupUser::getGroup)
                .toList();
    }

    @Override
    public List<Group> getGroups(String groupName) {
        return groupRepository.getGroups(groupName);
    }
}
