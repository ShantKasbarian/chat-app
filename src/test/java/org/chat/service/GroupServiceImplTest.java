package org.chat.service;

import jakarta.persistence.EntityManager;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.exception.InvalidGroupException;
import org.chat.exception.InvalidRoleException;
import org.chat.exception.UnableToJoinGroupException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.UserRepository;
import org.chat.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {
    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupUserRepository groupUserRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    private Group group;

    private User user1;

    private User user2;

    private GroupUser groupUser1;

    private GroupUser groupUser2;

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

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setPassword("Password123+");

        groupUser1 = new GroupUser(UUID.randomUUID(), group, user1, false, false);
        groupUser2 = new GroupUser(UUID.randomUUID(), group, user2, true, true);

        when(groupRepository.getEntityManager()).thenReturn(entityManager);
        when(userRepository.getEntityManager()).thenReturn(entityManager);
        when(groupUserRepository.getEntityManager()).thenReturn(entityManager);
    }

    @Test
    void createGroup() {
        when(groupRepository.existsByName(anyString())).thenReturn(false);
        doNothing().when(groupRepository).persist(group);

        when(userRepository.findById(any(UUID.class))).thenReturn(user1);

        doNothing().when(groupUserRepository).persist(anyList());

        Group response = groupService.createGroup(group, new UUID[]{user2.getId()}, user1.getId());

        assertEquals(group.getId(), response.getId());
        assertEquals(group.getName(), response.getName());
        verify(groupRepository).existsByName(anyString());
        verify(groupRepository).persist(group);
        verify(groupUserRepository).persist(anyList());
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWhenGroupNameIsNull() {
        group.setName(null);
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new UUID[]{}, user1.getId()));
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWithGroupNameEmpty() {
        group.setName("");
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new UUID[]{}, user1.getId()));
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWithGroupAlreadyExists() {
        when(groupRepository.existsByName(anyString())).thenReturn(true);
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new UUID[]{}, user1.getId()));
    }

    @Test
    void joinGroup() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(group);
        when(userRepository.findById(any(UUID.class))).thenReturn(user1);
        when(groupUserRepository.existsByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);

        doNothing().when(groupUserRepository).persist(any(GroupUser.class));

        GroupUser response = groupService.joinGroup(group.getId(), user1.getId());

        assertNotNull(response);
        assertEquals(group.getId(), response.getGroup().getId());
        assertEquals(user1.getId(), response.getUser().getId());
        verify(groupUserRepository).persist(any(GroupUser.class));
    }

    @Test
    void joinGroupShouldThrowUnableToJoinGroupException() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(group);
        when(userRepository.findById(any(UUID.class))).thenReturn(user1);
        when(groupUserRepository.existsByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        assertThrows(UnableToJoinGroupException.class, () -> groupService.joinGroup(group.getId(), user1.getId()));
    }

    @Test
    void leaveGroup() {
        String expectedResult = "you left the group";

        when(groupRepository.existsById(any(UUID.class))).thenReturn(true);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser1);
        doNothing().when(groupUserRepository).delete(groupUser1);

        String response = groupService.leaveGroup(group.getId(), user1.getId());

        assertEquals(expectedResult, response);
        verify(groupUserRepository).delete(groupUser1);
    }

    @Test
    void acceptJoinGroup() {
        when(groupUserRepository.findById(any(UUID.class)))
                .thenReturn(groupUser1);

        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser2);

        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser1);

        groupUser1.setIsCreator(true);

        when(entityManager.merge(any(GroupUser.class))).thenReturn(groupUser1);

        GroupUser response = groupService.acceptJoinGroup(user1.getId(), groupUser1.getId());

        assertNotNull(response);
        assertEquals(groupUser1.getId(), response.getId());
        assertTrue(groupUser2.getIsMember());
        verify(entityManager, times(1)).merge(groupUser1);
    }

    @Test
    void acceptJoinGroupShouldThrowInvalidRoleException() {
        groupUser1.setIsCreator(false);

        when(groupUserRepository.findById(any(UUID.class)))
                .thenReturn(groupUser2);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser1);

        assertThrows(InvalidRoleException.class, () -> groupService.acceptJoinGroup(user1.getId(), groupUser2.getId()));
    }

    @Test
    void acceptJoinGroupShouldThrowUnableJoinGroupExceptionWhenUserAlreadyMember() {
        groupUser1.setIsMember(true);
        groupUser1.setIsCreator(true);
        groupUser2.setIsMember(true);

        when(groupUserRepository.findById(any(UUID.class)))
                .thenReturn(groupUser2);

        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser1);

        assertThrows(UnableToJoinGroupException.class, () -> groupService.acceptJoinGroup(user1.getId(), groupUser2.getId()));
    }

    @Test
    void rejectJoinGroup() {
        String expectedResponse = "user has been rejected";

        groupUser1.setIsMember(true);
        groupUser1.setIsCreator(true);
        groupUser2.setIsMember(false);

        when(groupUserRepository.findById(any(UUID.class)))
                .thenReturn(groupUser2);

        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser1);

        doNothing().when(groupUserRepository).delete(any(GroupUser.class));

        String response = groupService.rejectJoinGroup(user1.getId(), groupUser1.getId());

        assertEquals(expectedResponse, response);
        verify(groupUserRepository).delete(any(GroupUser.class));
    }

    @Test
    void getWaitingUsers() {
        groupUser1.setIsMember(true);
        groupUser1.setIsCreator(true);
        groupUser2.setIsMember(false);

        List<GroupUser> users = new ArrayList<>();
        users.add(groupUser2);

        when(groupRepository.existsById(any(UUID.class))).thenReturn(true);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser1);
        when(groupUserRepository.getWaitingUsers(any(UUID.class))).thenReturn(users);

        List<GroupUser> response = groupService.getWaitingUsers(group.getId(), user2.getId());

        assertNotNull(response);
        assertEquals(users.size(), response.size());
    }

    @Test
    void getUserJoinedGroups() {
        List<GroupUser> groups = new ArrayList<>();
        groups.add(groupUser2);

        when(groupUserRepository.getUserGroups(any(UUID.class)))
                .thenReturn(groups);

        List<Group> response = groupService.getUserJoinedGroups(user2.getId());

        assertEquals(groups.size(), response.size());
    }

    @Test
    void getGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        Group group2 = new Group();
        group2.setName("group2");
        groups.add(group2);

        when(groupRepository.getGroups(anyString())).thenReturn(groups);

        List<Group> response = groupService.getGroups("gr");

        assertNotNull(response);
        assertEquals(groups.size(), response.size());
    }
}