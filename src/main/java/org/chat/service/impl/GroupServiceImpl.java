package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.exception.*;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.UserRepository;
import org.chat.service.GroupService;

import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final String INVALID_GROUP_NAME_MESSAGE = "Invalid group name";

    private static final String GROUP_USER_NOT_FOUND_MESSAGE = "group user not found";

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
        String groupName = group.getName();

        if (group.getName() == null || groupName.isEmpty()) {
            throw new InvalidGroupException(INVALID_GROUP_NAME_MESSAGE);
        }

        log.info("creating group with name {}", groupName);

        if (creators == null) {
            creators = new String[]{};
        }

        if (groupRepository.existsByName(group.getName())) {
            throw new InvalidGroupException(GROUP_ALREADY_EXISTS_MESSAGE);
        }

        group.setId(UUID.randomUUID().toString());
        groupRepository.persist(group);

        User currentUser = userRepository.findById(userId).get();

        List<GroupUser> creatorsList = new ArrayList<>(
            Arrays.stream(creators)
                .map(userRepository::findById)
                .filter(creator -> !creator.get().getId().equals(userId))
                .map(creator -> new GroupUser(UUID.randomUUID().toString(), group, creator.get(), true, true))
                .toList()
        );

        creatorsList.add(new GroupUser(UUID.randomUUID().toString(), group, currentUser, true, true));

        groupUserRepository.persist(creatorsList);

        log.info("created group with name {}", groupName);

        return group;
    }

    @Override
    @Transactional
    public GroupUser joinGroup(String groupId, String userId) {
        log.info("joining group with id {}", groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));

        if (groupUserRepository.existsByGroupIdUserId(group.getId(), userId)) {
            throw new UnableToJoinGroupException(ALREADY_MEMBER_OF_GROUP_MESSAGE);
        }

        User user = userRepository.findById(userId).get();

        GroupUser groupUser = new GroupUser(UUID.randomUUID().toString(), group, user, false, false);

        groupUserRepository.persist(groupUser);

        log.info("joined group with id {}", groupId);

        return groupUser;
    }

    @Override
    @Transactional
    public String leaveGroup(String groupId, String userId) {
        log.info("leaving group with id {}", groupId);

        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE);
        }

        groupUserRepository.delete(groupUserRepository.findByGroupIdUserId(groupId, userId));

        log.info("left group with id {}", groupId);

        return SUCCESSFUL_LEAVE_GROUP_MESSAGE;
    }

    @Override
    @Transactional
    public GroupUser acceptJoinGroup(String userId, String groupUserId) {
        log.info("accepting groupUser with id {} join request", groupUserId);

        GroupUser groupUser = groupUserRepository.findById(groupUserId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_USER_NOT_FOUND_MESSAGE));

        GroupUser creator = groupUserRepository.findByGroupIdUserId(
                groupUser.getGroup().getId(), userId
        );

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        if (groupUser.getIsMember()) {
            throw new UnableToJoinGroupException(TARGET_USER_ALREADY_MEMBER_OF_GROUP);
        }

        groupUser.setIsCreator(false);
        groupUser.setIsMember(true);

        groupUserRepository.getEntityManager().merge(groupUser);

        log.info("accepted groupUser with id {} join request", groupUserId);

        return groupUser;
    }

    @Override
    @Transactional
    public String rejectJoinGroup(String userId, String groupUserId) {
        log.info("rejecting groupUser with id {} join request", groupUserId);

        GroupUser groupUser = groupUserRepository.findById(groupUserId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_USER_NOT_FOUND_MESSAGE));

        GroupUser creator = groupUserRepository.findByGroupIdUserId(
                groupUser.getGroup().getId(), userId
        );

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        groupUserRepository.delete(groupUser);

        log.info("rejected member with id {} to join group", groupUserId);

        return USER_REJECTION_MESSAGE;
    }

    @Override
    public List<GroupUser> getWaitingUsers(String groupId, String creatorId) {
        log.info("fetching join requests of group with id {}", groupId);

        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE);
        }

        GroupUser creator = groupUserRepository.findByGroupIdUserId(groupId, creatorId);

        if (!creator.getIsCreator()) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        var users = groupUserRepository.getWaitingUsers(groupId);

        log.info("fetched join requests of group with id {}", groupId);

        return users;
    }

    @Override
    public List<Group> getUserJoinedGroups(String userId) {
        log.info("fetching joined groups of user with id {}", userId);

        var groups = groupUserRepository.getUserGroups(userId)
                .stream()
                .map(GroupUser::getGroup)
                .toList();

        log.info("fetched joined groups of user with id {}", userId);

        return groups;
    }

    @Override
    public List<Group> getGroups(String groupName) {
        log.info("fetching groups with name {}", groupName);

        var groups = groupRepository.getGroups(groupName);

        log.info("fetched groups with name {}", groupName);

        return groups;
    }
}
