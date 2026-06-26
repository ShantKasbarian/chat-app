package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.GroupConverter;
import org.chat.converter.GroupUserConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GroupControllerTest {
    @InjectMocks
    private GroupController groupController;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupConverter groupConverter;

    @Mock
    private GroupUserConverter groupUserConverter;

    @Mock
    private JsonWebToken jsonWebToken;

    @Mock
    private UserContext userContext;

    @Mock
    private PanacheQuery<Group> groupQuery;

    @Mock
    private PanacheQuery<GroupUser> groupUserQuery;

    private Group group;

    private GroupDto groupDto;

    private GroupUser groupUser;

    private GroupUserDto groupUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        groupDto = new GroupDto(group.getId(), group.getName());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        groupUser = new GroupUser(UUID.randomUUID(), group.getId(), user.getId(), user.getUsername(), GroupUser.Role.ADMIN);
        groupUserDto = new GroupUserDto(groupUser.getId(), group.getId(), user.getId(), user.getUsername(), groupUser.getRole());

        when(userContext.get()).thenReturn(new UserPrincipal(user.getId(), user.getUsername()));
    }

    @Test
    void create() {
        when(groupConverter.convertToModel(any(Group.class))).thenReturn(groupDto);
        when(groupService.createGroup(any(Group.class), any(UserPrincipal.class)))
                .thenReturn(group);
        when(groupConverter.convertToEntity(any(GroupDto.class))).thenReturn(group);

        var response = groupController.create(jsonWebToken, groupDto);

        assertNotNull(response);
        assertEquals(groupDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(groupConverter).convertToModel(any(Group.class));
        verify(groupService).createGroup(any(Group.class), any(UserPrincipal.class));
        verify(userContext).get();
        verify(groupConverter).convertToEntity(any(GroupDto.class));
    }

    @Test
    void joinGroup() {
        when(groupUserConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(groupService.joinGroup(any(UUID.class), any(UserPrincipal.class)))
                .thenReturn(groupUser);

        var response = groupController.joinGroup(group.getId());

        assertNotNull(response);
        assertEquals(groupUserDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(groupUserConverter).convertToModel(any(GroupUser.class));
        verify(groupService).joinGroup(any(UUID.class), any(UserPrincipal.class));
        verify(userContext).get();
    }

    @Test
    void leaveGroup() {
        doNothing().when(groupService).leaveGroup(any(UUID.class), any(UUID.class));

        groupController.leaveGroup(group.getId());

        verify(groupService).leaveGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void acceptUserToGroup() {
        when(groupUserConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(groupService.acceptJoinGroup(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser);

        var response = groupController.acceptUserToGroup(groupUser.getId());

        assertNotNull(response);
        assertEquals(groupUserDto, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupUserConverter).convertToModel(any(GroupUser.class));
        verify(groupService).acceptJoinGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void rejectUserFromGroup() {
        doNothing().when(groupService).rejectJoinGroup(any(UUID.class), any(UUID.class));

        groupController.rejectUserFromGroup(groupUser.getId());

        verify(groupService).rejectJoinGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void getWaitingUsers() {
        List<GroupUser> groupUsers = new ArrayList<>();
        groupUsers.add(groupUser);

        when(groupService.findUsersWithPendingRole(any(UUID.class), any(UUID.class), anyInt(), anyInt()))
                .thenReturn(groupUserQuery);
        when(groupUserQuery.list()).thenReturn(groupUsers);
        when(groupUserConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(groupUserQuery.count()).thenReturn(10L);
        when(groupUserQuery.pageCount()).thenReturn(1);

        var response = groupController.getWaitingUsers(group.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupService).findUsersWithPendingRole(any(UUID.class), any(UUID.class), anyInt(), anyInt());
        verify(groupUserConverter, times(groupUsers.size())).convertToModel(any(GroupUser.class));
    }

    @Test
    void getJoinedGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        when(groupService.getUserJoinedGroups(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(new PageDto<>(groups, 10, 1));
        when(groupConverter.convertToModel(any(Group.class))).thenReturn(groupDto);
        when(groupQuery.count()).thenReturn(10L);
        when(groupQuery.pageCount()).thenReturn(1);

        var response = groupController.getJoinedGroups(0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupService).getUserJoinedGroups(any(UUID.class), anyInt(), anyInt());
        verify(groupConverter, times(groups.size())).convertToModel(any(Group.class));
    }

    @Test
    void getGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        when(groupService.getGroups(anyString(), anyInt(), anyInt()))
                .thenReturn(groupQuery);
        when(groupConverter.convertToModel(any(Group.class))).thenReturn(groupDto);

        var response = groupController.getGroups("g", 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupService).getGroups(anyString(), anyInt(), anyInt());
        verify(groupConverter, times(groups.size())).convertToModel(any(Group.class));
    }
}
