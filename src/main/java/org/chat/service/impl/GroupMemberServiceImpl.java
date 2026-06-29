package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.exception.UnauthorizedException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupMemberRepository;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupMemberService;

import java.util.UUID;

import static org.chat.service.impl.GroupServiceImpl.REQUEST_NOT_AUTHORIZED;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class GroupMemberServiceImpl implements GroupMemberService {
    private static final String ALREADY_MEMBER_OF_GROUP_MESSAGE = "you're already a member of this group or have submitted a request to join group";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you're not a member of this group";

    private static final String GROUP_USER_NOT_FOUND_MESSAGE = "Group member not found";

    private static final String GROUP_NOT_FOUND_MESSAGE = "Group not found";

    private final GroupRepository groupRepository;

    private final GroupMemberRepository groupMemberRepository;

    @Override
    @Transactional
    public GroupMember joinGroup(UUID groupId, UserPrincipal userPrincipal) {
        log.info("joining group with id {}", groupId);

        Group group = groupRepository.findByIdOptional(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));
        UUID userId = userPrincipal.id();

        if (groupMemberRepository.existsByGroupIdUserId(group.getId(), userId)) {
            throw new ResourceAlreadyExistsException(ALREADY_MEMBER_OF_GROUP_MESSAGE);
        }

        GroupMember groupMember = new GroupMember(UUID.randomUUID(), groupId, userId, userPrincipal.username(), GroupMember.Role.PENDING);

        groupMemberRepository.persist(groupMember);

        log.info("joined group with id {}", groupId);

        return groupMember;
    }

    @Override
    @Transactional
    public void leaveGroup(UUID groupId, UUID userId) {
        log.info("leaving group with id {}", groupId);

        GroupMember groupMember = groupMemberRepository.findByGroupIdUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        groupMemberRepository.delete(groupMember);

        log.info("left group with id {}", groupId);
    }

    @Override
    @Transactional
    public GroupMember acceptJoinGroup(UUID userId, UUID groupMemberId) {
        log.info("accepting groupMember with id {} join request", groupMemberId);

        GroupMember groupMember = groupMemberRepository.findByIdOptional(groupMemberId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_USER_NOT_FOUND_MESSAGE));

        GroupMember admin = groupMemberRepository.findByGroupIdUserId(groupMember.getGroupId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!admin.getRole().equals(GroupMember.Role.ADMIN)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        groupMember.setRole(GroupMember.Role.MEMBER);
        groupMemberRepository.persist(groupMember);

        log.info("accepted groupMember with id {} join request", groupMemberId);

        return groupMember;
    }

    @Override
    @Transactional
    public void rejectJoinGroup(UUID userId, UUID groupMemberId) {
        log.info("rejecting groupMember with id {} join request", groupMemberId);

        GroupMember groupMember = groupMemberRepository.findByIdOptional(groupMemberId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_USER_NOT_FOUND_MESSAGE));

        GroupMember admin = groupMemberRepository.findByGroupIdUserId(groupMember.getGroupId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (!admin.getRole().equals(GroupMember.Role.ADMIN)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        groupMemberRepository.delete(groupMember);

        log.info("rejected groupMember with id {} to join group", groupMemberId);
    }

    @Override
    public PanacheQuery<GroupMember> findUsersByRole(UUID groupId, UUID userId, GroupMember.Role role, int page, int size) {
        log.info("fetching join requests of group with id {}", groupId);

        var users = groupMemberRepository.findByRole(groupId, role, page, size);

        log.info("fetched join requests of group with id {}", groupId);

        return users;
    }
}
