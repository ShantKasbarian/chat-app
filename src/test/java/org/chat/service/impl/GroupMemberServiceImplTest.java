package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.exception.ForbiddenException;
import org.chat.repository.GroupRepository;
import org.chat.repository.impl.GroupMemberRepositoryImpl;
import org.chat.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupMemberServiceImplTest {
    private static final String ALREADY_MEMBER_OF_GROUP_MESSAGE = "you're already a member of this group or have submitted a request to join group";

    private static final String REQUEST_NOT_AUTHORIZED = "You do not have the necessary permissions to perform this request";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you're not a member of this group";

    private static final String GROUP_USER_NOT_FOUND_MESSAGE = "Group member not found";

    private static final String GROUP_NOT_FOUND_MESSAGE = "Group not found";

    @InjectMocks
    private GroupMemberServiceImpl groupUserService;

    @Mock
    private GroupMemberRepositoryImpl groupUserRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private PanacheQuery<GroupMember> panacheQuery;

    private User user1;

    private GroupMember groupMember1;

    private GroupMember groupMember2;

    private Group group;

    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setPassword("Password123+");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setPassword("Password123+");

        groupMember1 = new GroupMember(UUID.randomUUID(), group.getId(), user1.getId(), user1.getUsername(), GroupMember.Role.ADMIN);
        groupMember2 = new GroupMember(UUID.randomUUID(), group.getId(), user2.getId(), user2.getUsername(), GroupMember.Role.PENDING);

        userPrincipal = new UserPrincipal(user1.getId(), user1.getUsername());
    }

    @Test
    void joinGroup() {
        when(groupRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(group));
        when(groupUserRepository.existsByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        doNothing().when(groupUserRepository).persist(any(GroupMember.class));

        GroupMember response = groupUserService.joinGroup(group.getId(), userPrincipal);

        assertNotNull(response);
        assertEquals(group.getId(), response.getGroupId());
        assertEquals(user1.getId(), response.getUserId());
        verify(groupRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).existsByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).persist(any(GroupMember.class));
    }

    @Test
    void joinGroupShouldThrowResourceNotFoundExceptionWhenGroupNotFound() {
        when(groupRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.joinGroup(group.getId(), userPrincipal));
        assertEquals(GROUP_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(groupRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void joinGroupShouldThrowResourceAlreadyExistsExceptionWhenUserIsPartOfGroup() {
        when(groupRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(group));
        when(groupUserRepository.existsByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> groupUserService.joinGroup(group.getId(), userPrincipal));
        assertEquals(ALREADY_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).existsByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void leaveGroup() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupMember1));
        doNothing().when(groupUserRepository).delete(any(GroupMember.class));

        groupUserService.leaveGroup(group.getId(), user1.getId());

        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).delete(any(GroupMember.class));
    }

    @Test
    void leaveGroupShouldThrowResourceNotFoundExceptionWhenGroupUserNotFound() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.leaveGroup(group.getId(), user1.getId()));

        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void acceptJoinRequest() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupMember2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupMember1));
        doNothing().when(groupUserRepository).persist(any(GroupMember.class));

        GroupMember response = groupUserService.acceptJoinRequest(user1.getId(), groupMember2.getId());

        assertNotNull(response);
        assertEquals(GroupMember.Role.MEMBER, response.getRole());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).update(any(GroupMember.class));
    }

    @Test
    void acceptJoinRequestShouldThrowResourceNotFoundExceptionWhenAdminNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.acceptJoinRequest(user1.getId(), groupMember2.getId()));
        assertEquals(GROUP_USER_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void acceptJoinRequestShouldThrowResourceNotFoundExceptionWhenRequestMemberNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupMember2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.acceptJoinRequest(user1.getId(), groupMember2.getId()));
        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void acceptJoinRequestShouldThrowForbiddenExceptionWhenRequestMemberIsNotAdmin() {
        groupMember1.setRole(GroupMember.Role.MEMBER);

        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupMember2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupMember1));

        Exception exception = assertThrows(ForbiddenException.class, () -> groupUserService.acceptJoinRequest(user1.getId(), groupMember2.getId()));
        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void rejectJoinRequest() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupMember2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupMember1));
        doNothing().when(groupUserRepository).delete(any(GroupMember.class));

        groupUserService.rejectJoinRequest(user1.getId(), groupMember1.getId());

        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).delete(any(GroupMember.class));
    }

    @Test
    void rejectJoinRequestShouldThrowResourceNotFoundExceptionWhenRequestMemberNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.rejectJoinRequest(user1.getId(), groupMember1.getId()));
        assertEquals(GROUP_USER_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void rejectJoinRequestShouldThrowResourceNotFoundExceptionWhenAdminNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupMember2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.rejectJoinRequest(user1.getId(), groupMember1.getId()));
        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void rejectJoinRequestShouldThrowForbiddenExceptionWhenRequestMemberIsNotAdmin() {
        groupMember1.setRole(GroupMember.Role.MEMBER);

        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupMember2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupMember1));

        Exception exception = assertThrows(ForbiddenException.class, () -> groupUserService.rejectJoinRequest(user1.getId(), groupMember1.getId()));
        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void findUsersByRole() {
        when(groupUserRepository.findByRole(any(UUID.class), any(GroupMember.Role.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);

        var response = groupUserService.findUsersByRole(group.getId(), user1.getId(), GroupMember.Role.MEMBER,0, 10);

        assertNotNull(response);
        verify(groupUserRepository).findByRole(any(UUID.class), any(GroupMember.Role.class), anyInt(), anyInt());
    }
}