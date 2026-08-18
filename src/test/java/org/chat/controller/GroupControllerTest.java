package org.chat.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.chat.converter.GroupConverter;
import org.chat.entity.Group;
import org.chat.entity.User;
import org.chat.model.GroupDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GroupControllerTest {
  @InjectMocks private GroupController groupController;

  @Mock private GroupService groupService;

  @Mock private GroupConverter groupConverter;

  @Mock private UserContext userContext;

  @Mock private PanacheQuery<Group> panacheQuery;

  private Group group;

  private GroupDto groupDto;

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

    when(userContext.get()).thenReturn(new UserPrincipal(user.getId(), user.getUsername()));
  }

  @Test
  void create() {
    when(groupConverter.convertToModel(any(Group.class))).thenReturn(groupDto);
    when(groupService.createGroup(any(Group.class), any(UserPrincipal.class))).thenReturn(group);
    when(groupConverter.convertToEntity(any(GroupDto.class))).thenReturn(group);

    var response = groupController.create(groupDto);

    assertNotNull(response);
    assertEquals(groupDto, response.getEntity());
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    verify(groupConverter).convertToModel(any(Group.class));
    verify(groupService).createGroup(any(Group.class), any(UserPrincipal.class));
    verify(userContext).get();
    verify(groupConverter).convertToEntity(any(GroupDto.class));
  }

  @Test
  void getJoinedGroups() {
    List<Group> groups = new ArrayList<>();
    groups.add(group);

    when(groupService.getUserJoinedGroups(any(UUID.class), anyInt(), anyInt()))
        .thenReturn(new PageDto<>(groups, 10, 1));
    when(groupConverter.convertToModel(any(Group.class))).thenReturn(groupDto);
    when(panacheQuery.count()).thenReturn(10L);
    when(panacheQuery.pageCount()).thenReturn(1);

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

    when(groupService.getGroups(anyString(), anyInt(), anyInt())).thenReturn(panacheQuery);
    when(groupConverter.convertToModel(any(Group.class))).thenReturn(groupDto);
    when(panacheQuery.list()).thenReturn(groups);
    when(panacheQuery.count()).thenReturn(10L);
    when(panacheQuery.pageCount()).thenReturn(1);

    var response = groupController.getGroups("g", 0, 10);

    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    verify(groupService).getGroups(anyString(), anyInt(), anyInt());
    verify(groupConverter, atLeast(1)).convertToModel(any(Group.class));
  }
}
