package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.exception.UnauthorizedException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupUserService;

import java.util.UUID;

import static org.chat.service.impl.GroupServiceImpl.REQUEST_NOT_AUTHORIZED;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupUserServiceImpl implements GroupUserService {
    private static final String ALREADY_MEMBER_OF_GROUP_MESSAGE = "you're already a member of this group or have submitted a request to join group";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you're not a member of this group";

    private static final String GROUP_USER_NOT_FOUND_MESSAGE = "Group user not found";

    private static final String GROUP_NOT_FOUND_MESSAGE = "Group not found";

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    @Override
    @Transactional
    public GroupUser joinGroup(UUID groupId, UserPrincipal userPrincipal) {
        log.info("joining group with id {}", groupId);

        Group group = groupRepository.findByIdOptional(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));
        UUID userId = userPrincipal.id();

        if (groupUserRepository.existsByGroupIdUserId(group.getId(), userId)) {
            throw new ResourceAlreadyExistsException(ALREADY_MEMBER_OF_GROUP_MESSAGE);
        }

        GroupUser groupUser = new GroupUser(UUID.randomUUID(), groupId, userId, userPrincipal.username(), GroupUser.Role.PENDING);

        groupUserRepository.persist(groupUser);

        log.info("joined group with id {}", groupId);

        return groupUser;
    }

    @Override
    @Transactional
    public void leaveGroup(UUID groupId, UUID userId) {
        log.info("leaving group with id {}", groupId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        groupUserRepository.delete(groupUser);

        log.info("left group with id {}", groupId);
    }

    @Override
    @Transactional
    public GroupUser acceptJoinGroup(UUID userId, UUID groupUserId) {
        log.info("accepting groupUser with id {} join request", groupUserId);

        GroupUser groupUser = groupUserRepository.findByIdOptional(groupUserId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_USER_NOT_FOUND_MESSAGE));

        GroupUser admin = groupUserRepository.findByGroupIdUserId(groupUser.getGroupId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!admin.getRole().equals(GroupUser.Role.ADMIN)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        groupUser.setRole(GroupUser.Role.MEMBER);
        groupUserRepository.persist(groupUser);

        log.info("accepted groupUser with id {} join request", groupUserId);

        return groupUser;
    }

    @Override
    @Transactional
    public void rejectJoinGroup(UUID userId, UUID groupUserId) {
        log.info("rejecting groupUser with id {} join request", groupUserId);

        GroupUser groupUser = groupUserRepository.findByIdOptional(groupUserId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_USER_NOT_FOUND_MESSAGE));

        GroupUser admin = groupUserRepository.findByGroupIdUserId(groupUser.getGroupId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!admin.getRole().equals(GroupUser.Role.ADMIN)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        groupUserRepository.delete(groupUser);

        log.info("rejected member with id {} to join group", groupUserId);
    }

    @Override
    public PanacheQuery<GroupUser> findUsersByRole(UUID groupId, UUID userId, GroupUser.Role role, int page, int size) {
        log.info("fetching join requests of group with id {}", groupId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!groupUser.getRole().equals(GroupUser.Role.ADMIN)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        var users = groupUserRepository.findByRole(groupId, role, page, size);

        log.info("fetched join requests of group with id {}", groupId);

        return users;
    }
}
