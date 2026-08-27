package org.chat.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.quarkus.mongodb.panache.PanacheQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.model.GroupDto;
import org.chat.repository.GroupMemberRepository;
import org.chat.repository.GroupRepository;
import org.chat.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GroupServiceImplTest {
  private static final String GROUP_ALREADY_EXISTS_MESSAGE = "Group already exists";

  @InjectMocks private GroupServiceImpl groupService;

  @Mock private GroupRepository groupRepository;

  @Mock private GroupMemberRepository groupMemberRepository;

  @Mock private PanacheQuery<Group> groupQuery;

  @Mock private PanacheQuery<GroupMember> groupUserQuery;

  private Group group;

  private GroupDto groupDto;

  private User user2;

  private UserPrincipal userPrincipal;

  private GroupMember groupMember1;

  private GroupMember groupMember2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    group = new Group();
    group.setId(UUID.randomUUID());
    group.setName("group");

    groupDto = new GroupDto(group.getId(), group.getName());

    User user1 = new User();
    user1.setId(UUID.randomUUID());
    user1.setUsername("user1");
    user1.setPassword("Password123+");

    user2 = new User();
    user2.setId(UUID.randomUUID());
    user2.setUsername("user2");
    user2.setPassword("Password123+");

    groupMember1 =
        new GroupMember(
            UUID.randomUUID(),
            group.getId(),
            user1.getId(),
            user1.getUsername(),
            GroupMember.Role.ADMIN);
    groupMember2 =
        new GroupMember(
            UUID.randomUUID(),
            group.getId(),
            user2.getId(),
            user2.getUsername(),
            GroupMember.Role.PENDING);

    userPrincipal = new UserPrincipal(user1.getId(), user1.getUsername());
  }

  @Test
  void createGroup() {
    when(groupRepository.existsByName(anyString())).thenReturn(false);
    doNothing().when(groupRepository).persist(any(Group.class));
    doNothing().when(groupMemberRepository).persist(any(GroupMember.class));

    Group response = groupService.createGroup(groupDto, userPrincipal);

    assertEquals(group.getName(), response.getName());
    verify(groupRepository).existsByName(anyString());
    verify(groupRepository).persist(any(Group.class));
    verify(groupMemberRepository).persist(any(GroupMember.class));
  }

  @Test
  void createGroupShouldThrowResourceAlreadyExistsExceptionWithGroupAlreadyExists() {
    when(groupRepository.existsByName(anyString())).thenReturn(true);

    Exception exception =
        assertThrows(
            ResourceAlreadyExistsException.class,
            () -> groupService.createGroup(groupDto, userPrincipal));
    assertEquals(GROUP_ALREADY_EXISTS_MESSAGE, exception.getMessage());
  }

  @Test
  void getUserJoinedGroups() {
    List<Group> groups = new ArrayList<>();
    groups.add(group);

    when(groupMemberRepository.findByUserId(any(UUID.class), anyInt(), anyInt()))
        .thenReturn(groupUserQuery);
    when(groupUserQuery.list()).thenReturn(List.of(groupMember1, groupMember2));
    when(groupRepository.findByIds(anyList())).thenReturn(groups);
    when(groupUserQuery.count()).thenReturn(10L);
    when(groupUserQuery.pageCount()).thenReturn(1);

    var response = groupService.getUserJoinedGroups(user2.getId(), 0, 10);

    assertNotNull(response);
  }

  @Test
  void getGroups() {
    when(groupRepository.findByName(anyString(), anyInt(), anyInt())).thenReturn(groupQuery);

    var response = groupService.getGroups("gr", 0, 10);

    assertNotNull(response);
  }
}
