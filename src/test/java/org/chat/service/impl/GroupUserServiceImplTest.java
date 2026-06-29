package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.exception.UnauthorizedException;
import org.chat.repository.GroupRepository;
import org.chat.repository.impl.GroupUserRepositoryImpl;
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

class GroupUserServiceImplTest {
    private static final String ALREADY_MEMBER_OF_GROUP_MESSAGE = "you're already a member of this group or have submitted a request to join group";

    private static final String REQUEST_NOT_AUTHORIZED = "You do not have the necessary permissions to perform this request";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you're not a member of this group";

    private static final String GROUP_USER_NOT_FOUND_MESSAGE = "Group user not found";

    private static final String GROUP_NOT_FOUND_MESSAGE = "Group not found";

    @InjectMocks
    private GroupUserServiceImpl groupUserService;

    @Mock
    private GroupUserRepositoryImpl groupUserRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private PanacheQuery<GroupUser> panacheQuery;

    private User user1;

    private GroupUser groupUser1;

    private GroupUser groupUser2;

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

        groupUser1 = new GroupUser(UUID.randomUUID(), group.getId(), user1.getId(), user1.getUsername(), GroupUser.Role.ADMIN);
        groupUser2 = new GroupUser(UUID.randomUUID(), group.getId(), user2.getId(), user2.getUsername(), GroupUser.Role.PENDING);

        userPrincipal = new UserPrincipal(user1.getId(), user1.getUsername());
    }

    @Test
    void joinGroup() {
        when(groupRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(group));
        when(groupUserRepository.existsByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        doNothing().when(groupUserRepository).persist(any(GroupUser.class));

        GroupUser response = groupUserService.joinGroup(group.getId(), userPrincipal);

        assertNotNull(response);
        assertEquals(group.getId(), response.getGroupId());
        assertEquals(user1.getId(), response.getUserId());
        verify(groupRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).existsByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).persist(any(GroupUser.class));
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
                .thenReturn(Optional.of(groupUser1));
        doNothing().when(groupUserRepository).delete(any(GroupUser.class));

        groupUserService.leaveGroup(group.getId(), user1.getId());

        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).delete(any(GroupUser.class));
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
    void acceptJoinGroup() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser1));
        doNothing().when(groupUserRepository).persist(any(GroupUser.class));

        GroupUser response = groupUserService.acceptJoinGroup(user1.getId(), groupUser2.getId());

        assertNotNull(response);
        assertEquals(GroupUser.Role.MEMBER, response.getRole());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).persist(any(GroupUser.class));
    }

    @Test
    void acceptJoinGroupShouldThrowResourceNotFoundExceptionWhenAdminNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.acceptJoinGroup(user1.getId(), groupUser2.getId()));
        assertEquals(GROUP_USER_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void acceptJoinGroupShouldThrowResourceNotFoundExceptionWhenGroupUserNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.acceptJoinGroup(user1.getId(), groupUser2.getId()));
        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void acceptJoinGroupShouldThrowUnauthorizedExceptionWhenGroupUserIsNotAdmin() {
        groupUser1.setRole(GroupUser.Role.MEMBER);

        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser1));

        Exception exception = assertThrows(UnauthorizedException.class, () -> groupUserService.acceptJoinGroup(user1.getId(), groupUser2.getId()));
        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void rejectJoinGroup() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser1));
        doNothing().when(groupUserRepository).delete(any(GroupUser.class));

        groupUserService.rejectJoinGroup(user1.getId(), groupUser1.getId());

        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).delete(any(GroupUser.class));
    }

    @Test
    void rejectJoinGroupShouldThrowResourceNotFoundExceptionWhenGroupUserNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.rejectJoinGroup(user1.getId(), groupUser1.getId()));
        assertEquals(GROUP_USER_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void rejectJoinGroupShouldThrowResourceNotFoundExceptionWhenAdminNotFound() {
        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.rejectJoinGroup(user1.getId(), groupUser1.getId()));
        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void rejectJoinGroupShouldThrowUnauthorizedExceptionWhenGroupUserIsNotAdmin() {
        groupUser1.setRole(GroupUser.Role.MEMBER);

        when(groupUserRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser1));

        Exception exception = assertThrows(UnauthorizedException.class, () -> groupUserService.rejectJoinGroup(user1.getId(), groupUser1.getId()));
        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
        verify(groupUserRepository).findByIdOptional(any(UUID.class));
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void findUsersByRole() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser1));
        when(groupUserRepository.findByRole(any(UUID.class), any(GroupUser.Role.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);

        var response = groupUserService.findUsersByRole(group.getId(), user1.getId(), GroupUser.Role.MEMBER,0, 10);

        assertNotNull(response);
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(groupUserRepository).findByRole(any(UUID.class), any(GroupUser.Role.class), anyInt(), anyInt());
    }

    @Test
    void findUsersByRoleShouldThrowResourceNotFoundExceptionWhenGroupUserNotFound() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> groupUserService.findUsersByRole(group.getId(), user1.getId(), GroupUser.Role.MEMBER,0, 10));

        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void findUsersByRoleShouldThrowUnauthorizedExceptionWhenGroupUserIsNotAdmin() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser2));

        Exception exception = assertThrows(UnauthorizedException.class, () -> groupUserService.findUsersByRole(group.getId(), user1.getId(), GroupUser.Role.MEMBER,0, 10));

        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }
}