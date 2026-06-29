package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.GroupUserConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class GroupUserControllerTest {
    @InjectMocks
    private GroupUserController groupUserController;

    @Mock
    private GroupUserService groupUserService;

    @Mock
    private GroupUserConverter groupUserConverter;

    @Mock
    private PanacheQuery<GroupUser> panacheQuery;

    @Mock
    private UserContext userContext;

    private GroupUser groupUser;

    private GroupUserDto groupUserDto;

    private Group group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        groupUser = new GroupUser(UUID.randomUUID(), group.getId(), user.getId(), user.getUsername(), GroupUser.Role.ADMIN);
        groupUserDto = new GroupUserDto(groupUser.getId(), group.getId(), user.getId(), user.getUsername(), groupUser.getRole());

        when(userContext.get()).thenReturn(new UserPrincipal(user.getId(), user.getUsername()));
    }

    @Test
    void joinGroup() {
        when(groupUserConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(groupUserService.joinGroup(any(UUID.class), any(UserPrincipal.class)))
                .thenReturn(groupUser);

        var response = groupUserController.joinGroup(group.getId());

        assertNotNull(response);
        assertEquals(groupUserDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(groupUserConverter).convertToModel(any(GroupUser.class));
        verify(groupUserService).joinGroup(any(UUID.class), any(UserPrincipal.class));
        verify(userContext).get();
    }

    @Test
    void leaveGroup() {
        doNothing().when(groupUserService).leaveGroup(any(UUID.class), any(UUID.class));

        groupUserController.leaveGroup(group.getId());

        verify(groupUserService).leaveGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void acceptUserToGroup() {
        when(groupUserConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(groupUserService.acceptJoinGroup(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser);

        var response = groupUserController.acceptUserToGroup(groupUser.getId());

        assertNotNull(response);
        assertEquals(groupUserDto, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupUserConverter).convertToModel(any(GroupUser.class));
        verify(groupUserService).acceptJoinGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void rejectUserFromGroup() {
        doNothing().when(groupUserService).rejectJoinGroup(any(UUID.class), any(UUID.class));

        groupUserController.rejectUserFromGroup(groupUser.getId());

        verify(groupUserService).rejectJoinGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void getUsersByRole() {
        List<GroupUser> groupUsers = new ArrayList<>();
        groupUsers.add(groupUser);

        when(groupUserService.findUsersByRole(any(UUID.class), any(UUID.class), any(GroupUser.Role.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(groupUsers);
        when(groupUserConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(panacheQuery.count()).thenReturn(10L);
        when(panacheQuery.pageCount()).thenReturn(1);

        var response = groupUserController.getUsersByRole(group.getId(), GroupUser.Role.MEMBER, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupUserService).findUsersByRole(any(UUID.class), any(UUID.class), any(GroupUser.Role.class), anyInt(), anyInt());
        verify(groupUserConverter, times(groupUsers.size())).convertToModel(any(GroupUser.class));
    }
}