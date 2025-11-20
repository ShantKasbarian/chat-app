package org.chat.controller;

import jakarta.ws.rs.core.Response;
import org.chat.converter.ToEntityConverter;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
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
    private ToModelConverter<GroupDto, Group> groupToModelConverter;

    @Mock
    private ToEntityConverter<Group, GroupDto> groupDtoToEntityConverter;

    @Mock
    private ToModelConverter<GroupUserDto, GroupUser> groupUserToModelConverter;

    @Mock
    private JsonWebToken jsonWebToken;

    private Group group;

    private GroupDto groupDto;

    private User user;

    private GroupUser groupUser;

    private GroupUserDto groupUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        groupDto = new GroupDto(group.getId(), group.getName(), new UUID[]{});

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        groupUser = new GroupUser(UUID.randomUUID(), group, user, false, false);
        groupUserDto = new GroupUserDto(groupUser.getId(), group.getId(), group.getName(), user.getId(), user.getUsername(), groupUser.getIsMember(), groupUser.getIsCreator());

        when(jsonWebToken.getClaim(anyString())).thenReturn(user.getId().toString());
    }

    @Test
    void create() {
        when(groupToModelConverter.convertToModel(any(Group.class))).thenReturn(groupDto);
        when(groupService.createGroup(any(Group.class), (UUID[]) any(UUID.class.arrayType()), any(UUID.class)))
                .thenReturn(group);
        when(groupDtoToEntityConverter.convertToEntity(any(GroupDto.class))).thenReturn(group);

        var response = groupController.create(jsonWebToken, groupDto);

        assertNotNull(response);
        assertEquals(groupDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(groupToModelConverter).convertToModel(any(Group.class));
        verify(groupService).createGroup(any(Group.class), (UUID[]) any(UUID.class.arrayType()), any(UUID.class));
        verify(groupDtoToEntityConverter).convertToEntity(any(GroupDto.class));
    }

    @Test
    void joinGroup() {
        when(groupUserToModelConverter.convertToModel(any(GroupUser.class))).thenReturn(groupUserDto);
        when(groupService.joinGroup(any(UUID.class), any(UUID.class))).thenReturn(groupUser);

        var response = groupController.joinGroup(group.getId());

        assertNotNull(response);
        assertEquals(groupUserDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(groupUserToModelConverter).convertToModel(any(GroupUser.class));
        verify(groupService).joinGroup(any(UUID.class), any(UUID.class));
    }

    @Test
    void leaveGroup() {
        when(groupService.leaveGroup(any(UUID.class), any(UUID.class))).thenReturn("");

        groupController.leaveGroup(group.getId());

        verify(groupService).leaveGroup(any(UUID.class), any(UUID.class));
    }

    @Test
    void acceptUserToGroup() {
        when(groupUserToModelConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);
        when(groupService.acceptJoinGroup(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser);

        var response = groupController.acceptUserToGroup(groupUser.getId());

        assertNotNull(response);
        assertEquals(groupUserDto, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupUserToModelConverter).convertToModel(any(GroupUser.class));
        verify(groupService).acceptJoinGroup(any(UUID.class), any(UUID.class));
    }

    @Test
    void rejectUserFromGroup() {
        when(groupService.rejectJoinGroup(any(UUID.class), any(UUID.class))).thenReturn("");

        groupController.rejectUserFromGroup(groupUser.getId());

        verify(groupService).rejectJoinGroup(any(UUID.class), any(UUID.class));
    }

    @Test
    void getWaitingUsers() {
        List<GroupUser> groupUsers = new ArrayList<>();
        groupUsers.add(groupUser);

        when(groupService.getWaitingUsers(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUsers);
        when(groupUserToModelConverter.convertToModel(any(GroupUser.class)))
                .thenReturn(groupUserDto);

        var response = groupController.getWaitingUsers(group.getId());

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupService).getWaitingUsers(any(UUID.class), any(UUID.class));
    }

    @Test
    void getJoinedGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        when(groupService.getUserJoinedGroups(any(UUID.class))).thenReturn(groups);
        when(groupToModelConverter.convertToModel(any(Group.class))).thenReturn(groupDto);

        var response = groupController.getJoinedGroups();

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupService).getUserJoinedGroups(any(UUID.class));
    }

    @Test
    void getGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        when(groupService.getGroups(anyString())).thenReturn(groups);
        when(groupToModelConverter.convertToModel(any(Group.class))).thenReturn(groupDto);

        var response = groupController.getGroups("g");

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupService).getGroups(anyString());
    }
}
