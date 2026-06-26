package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.exception.*;
import org.chat.model.PageDto;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.service.GroupService;

import java.util.*;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final String INVALID_GROUP_NAME_MESSAGE = "Invalid group name";

    private static final String GROUP_ALREADY_EXISTS_MESSAGE = "Group already exists";

    private static final String ALREADY_MEMBER_OF_GROUP_MESSAGE = "you're already a member of this group or have submitted a request to join group";

    private static final String SUCCESSFUL_LEAVE_GROUP_MESSAGE = "you left the group";

    private static final String REQUEST_NOT_AUTHORIZED = "You do not have the necessary permissions to perform this request";

    private static final String USER_REJECTION_MESSAGE = "user has been rejected";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you're not a member of this group";

    private static final String GROUP_NOT_FOUND_MESSAGE = "Group not found";

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    @Override
    @Transactional
    public Group createGroup(Group group, UUID userId, String username) {
        String groupName = group.getName();

        if (group.getName() == null || groupName.isEmpty()) {
            throw new InvalidGroupException(INVALID_GROUP_NAME_MESSAGE);
        }

        log.info("creating group with name {}", groupName);

        if (groupRepository.existsByName(group.getName())) {
            throw new InvalidGroupException(GROUP_ALREADY_EXISTS_MESSAGE);
        }

        UUID groupId = UUID.randomUUID();
        group.setId(groupId);
        groupRepository.persist(group);

        GroupUser groupUser = new GroupUser(UUID.randomUUID(), groupId, userId, username, GroupUser.Role.ADMIN);
        groupUserRepository.persist(groupUser);

        log.info("created group with name {}", groupName);

        return group;
    }

    @Override
    @Transactional
    public GroupUser joinGroup(UUID groupId, UUID userId) {
        log.info("joining group with id {}", groupId);

        Group group = groupRepository.findById(groupId);

        if (groupUserRepository.existsByGroupIdUserId(group.getId(), userId)) {
            throw new UnableToJoinGroupException(ALREADY_MEMBER_OF_GROUP_MESSAGE);
        }

        GroupUser groupUser = new GroupUser();
        groupUser.setId(UUID.randomUUID());
        groupUser.setGroupId(groupId);
        groupUser.setUserId(userId);
        groupUser.setRole(GroupUser.Role.PENDING);

        groupUserRepository.persist(groupUser);

        log.info("joined group with id {}", groupId);

        return groupUser;
    }

    @Override
    @Transactional
    public String leaveGroup(UUID groupId, UUID userId) {
        log.info("leaving group with id {}", groupId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        groupUserRepository.delete(groupUser);

        log.info("left group with id {}", groupId);

        return SUCCESSFUL_LEAVE_GROUP_MESSAGE;
    }

    @Override
    @Transactional
    public GroupUser acceptJoinGroup(UUID userId, UUID groupUserId) {
        log.info("accepting groupUser with id {} join request", groupUserId);

        GroupUser groupUser = groupUserRepository.findById(groupUserId);

        GroupUser creator = groupUserRepository.findByGroupIdUserId(groupUser.getGroupId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!creator.getRole().equals(GroupUser.Role.ADMIN)) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        groupUser.setRole(GroupUser.Role.MEMBER);
        groupUserRepository.persist(groupUser);

        log.info("accepted groupUser with id {} join request", groupUserId);

        return groupUser;
    }

    @Override
    @Transactional
    public String rejectJoinGroup(UUID userId, UUID groupUserId) {
        log.info("rejecting groupUser with id {} join request", groupUserId);

        GroupUser groupUser = groupUserRepository.findById(groupUserId);

        GroupUser creator = groupUserRepository.findByGroupIdUserId(groupUser.getGroupId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!creator.getRole().equals(GroupUser.Role.ADMIN)) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        groupUserRepository.delete(groupUser);

        log.info("rejected member with id {} to join group", groupUserId);

        return USER_REJECTION_MESSAGE;
    }

    @Override
    public PanacheQuery<GroupUser> getWaitingUsers(UUID groupId, UUID creatorId, int page, int size) {
        log.info("fetching join requests of group with id {}", groupId);

        GroupUser creator = groupUserRepository.findByGroupIdUserId(groupId, creatorId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (creator.getRole().equals(GroupUser.Role.ADMIN)) {
            throw new InvalidRoleException(REQUEST_NOT_AUTHORIZED);
        }

        var users = groupUserRepository.findByRole(groupId, GroupUser.Role.PENDING, page, size);

        log.info("fetched join requests of group with id {}", groupId);

        return users;
    }

    @Override
    public PageDto<Group> getUserJoinedGroups(UUID userId, int page, int size) {
        log.info("fetching joined groups of user with id {}", userId);

        var query = groupUserRepository.findByUserId(userId, page, size);
        var ids = query.list()
                .stream()
                .map(GroupUser::getId)
                .toList();
        var groups = new PageDto<>(groupRepository.findByIds(ids), query.count(), query.pageCount());

        log.info("fetched joined groups of user with id {}", userId);

        return groups;
    }

    @Override
    public PanacheQuery<Group> getGroups(String groupName, int page, int size) {
        log.info("fetching groups with name {}", groupName);

        var groups = groupRepository.findByName(groupName, page, size);

        log.info("fetched groups with name {}", groupName);

        return groups;
    }
}
