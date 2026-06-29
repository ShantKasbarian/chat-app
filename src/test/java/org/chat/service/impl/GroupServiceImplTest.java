package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.exception.UnauthorizedException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {
    private static final String GROUP_ALREADY_EXISTS_MESSAGE = "Group already exists";

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupUserRepository groupUserRepository;

    @Mock
    private PanacheQuery<Group> groupQuery;

    @Mock
    private PanacheQuery<GroupUser> groupUserQuery;

    private Group group;

    private User user2;

    private UserPrincipal userPrincipal;

    private GroupUser groupUser1;

    private GroupUser groupUser2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setPassword("Password123+");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setPassword("Password123+");

        groupUser1 = new GroupUser(UUID.randomUUID(), group.getId(), user1.getId(), user1.getUsername(), GroupUser.Role.ADMIN);
        groupUser2 = new GroupUser(UUID.randomUUID(), group.getId(), user2.getId(), user2.getUsername(), GroupUser.Role.PENDING);

        userPrincipal = new UserPrincipal(user1.getId(), user1.getUsername());
    }

    @Test
    void createGroup() {
        when(groupRepository.existsByName(anyString())).thenReturn(false);
        doNothing().when(groupRepository).persist(any(Group.class));
        doNothing().when(groupUserRepository).persist(any(GroupUser.class));

        Group response = groupService.createGroup(group, userPrincipal);

        assertEquals(group.getId(), response.getId());
        assertEquals(group.getName(), response.getName());
        verify(groupRepository).existsByName(anyString());
        verify(groupRepository).persist(any(Group.class));
        verify(groupUserRepository).persist(any(GroupUser.class));
    }

    @Test
    void createGroupShouldThrowResourceAlreadyExistsExceptionWithGroupAlreadyExists() {
        when(groupRepository.existsByName(anyString())).thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> groupService.createGroup(group, userPrincipal));
        assertEquals(GROUP_ALREADY_EXISTS_MESSAGE, exception.getMessage());
    }

    @Test
    void getUserJoinedGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        when(groupUserRepository.findByUserId(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(groupUserQuery);
        when(groupUserQuery.list()).thenReturn(List.of(groupUser1, groupUser2));
        when(groupRepository.findByIds(anyList())).thenReturn(groups);
        when(groupUserQuery.count()).thenReturn(10L);
        when(groupUserQuery.pageCount()).thenReturn(1);

        var response = groupService.getUserJoinedGroups(user2.getId(), 0, 10);

        assertNotNull(response);
    }

    @Test
    void getGroups() {
        when(groupRepository.findByName(anyString(), anyInt(), anyInt()))
                .thenReturn(groupQuery);

        var response = groupService.getGroups("gr", 0, 10);

        assertNotNull(response);
    }
}
