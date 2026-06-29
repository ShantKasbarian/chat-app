package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.GroupMemberConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.User;
import org.chat.model.GroupMemberDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupMemberService;
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

class GroupMemberControllerTest {
    @InjectMocks
    private GroupMemberController groupMemberController;

    @Mock
    private GroupMemberService groupMemberService;

    @Mock
    private GroupMemberConverter groupMemberConverter;

    @Mock
    private PanacheQuery<GroupMember> panacheQuery;

    @Mock
    private UserContext userContext;

    private GroupMember groupMember;

    private GroupMemberDto groupMemberDto;

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

        groupMember = new GroupMember(UUID.randomUUID(), group.getId(), user.getId(), user.getUsername(), GroupMember.Role.ADMIN);
        groupMemberDto = new GroupMemberDto(groupMember.getId(), group.getId(), user.getId(), user.getUsername(), groupMember.getRole());

        when(userContext.get()).thenReturn(new UserPrincipal(user.getId(), user.getUsername()));
    }

    @Test
    void joinGroup() {
        when(groupMemberConverter.convertToModel(any(GroupMember.class)))
                .thenReturn(groupMemberDto);
        when(groupMemberService.joinGroup(any(UUID.class), any(UserPrincipal.class)))
                .thenReturn(groupMember);

        var response = groupMemberController.joinGroup(group.getId());

        assertNotNull(response);
        assertEquals(groupMemberDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(groupMemberConverter).convertToModel(any(GroupMember.class));
        verify(groupMemberService).joinGroup(any(UUID.class), any(UserPrincipal.class));
        verify(userContext).get();
    }

    @Test
    void leaveGroup() {
        doNothing().when(groupMemberService).leaveGroup(any(UUID.class), any(UUID.class));

        groupMemberController.leaveGroup(group.getId());

        verify(groupMemberService).leaveGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void acceptUserToGroup() {
        when(groupMemberConverter.convertToModel(any(GroupMember.class)))
                .thenReturn(groupMemberDto);
        when(groupMemberService.acceptJoinGroup(any(UUID.class), any(UUID.class)))
                .thenReturn(groupMember);

        var response = groupMemberController.acceptUserToGroup(groupMember.getId());

        assertNotNull(response);
        assertEquals(groupMemberDto, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupMemberConverter).convertToModel(any(GroupMember.class));
        verify(groupMemberService).acceptJoinGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void rejectUserFromGroup() {
        doNothing().when(groupMemberService).rejectJoinGroup(any(UUID.class), any(UUID.class));

        groupMemberController.rejectUserFromGroup(groupMember.getId());

        verify(groupMemberService).rejectJoinGroup(any(UUID.class), any(UUID.class));
        verify(userContext).get();
    }

    @Test
    void getUsersByRole() {
        List<GroupMember> groupMembers = new ArrayList<>();
        groupMembers.add(groupMember);

        when(groupMemberService.findUsersByRole(any(UUID.class), any(UUID.class), any(GroupMember.Role.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(groupMembers);
        when(groupMemberConverter.convertToModel(any(GroupMember.class)))
                .thenReturn(groupMemberDto);
        when(panacheQuery.count()).thenReturn(10L);
        when(panacheQuery.pageCount()).thenReturn(1);

        var response = groupMemberController.getUsersByRole(group.getId(), GroupMember.Role.MEMBER, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(groupMemberService).findUsersByRole(any(UUID.class), any(UUID.class), any(GroupMember.Role.class), anyInt(), anyInt());
        verify(groupMemberConverter, times(groupMembers.size())).convertToModel(any(GroupMember.class));
    }
}