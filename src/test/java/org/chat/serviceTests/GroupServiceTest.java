package org.chat.serviceTests;

import jakarta.persistence.EntityManager;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.User;
import org.chat.exceptions.InvalidGroupException;
import org.chat.exceptions.InvalidRoleException;
import org.chat.exceptions.UnableToJoinGroupException;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.UserRepository;
import org.chat.services.GroupService;
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

class GroupServiceTest {
    @InjectMocks
    private GroupService groupService;

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

    private User user3;

    private GroupUser groupUser1;

    private GroupUser groupUser2;

    private GroupUser groupUser3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setUsername("user1");
        user1.setPassword("Password123+");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setUsername("user2");
        user2.setPassword("Password123+");

        user3 = new User();
        user3.setId(UUID.randomUUID().toString());
        user3.setUsername("user3");
        user3.setUsername("Password123+");

        groupUser1 = new GroupUser(UUID.randomUUID().toString(), group, user1, false, false);
        groupUser2 = new GroupUser(UUID.randomUUID().toString(), group, user2, true, true);
        groupUser3 = new GroupUser(UUID.randomUUID().toString(), group, user3, false, true);

        when(groupRepository.getEntityManager()).thenReturn(entityManager);
        when(userRepository.getEntityManager()).thenReturn(entityManager);
        when(groupUserRepository.getEntityManager()).thenReturn(entityManager);
    }

    @Test
    void createGroup() {
        when(groupRepository.findByName(group.getName())).thenReturn(null);
        doNothing().when(groupRepository).persist(group);

        when(userRepository.findById(user1.getId())).thenReturn(user1);

        when(userRepository.findByUsername(user2.getUsername())).thenReturn(user2);

        groupUser1.setIsCreator(true);
        groupUser1.setIsMember(true);

        List<GroupUser> groupUsers = new ArrayList<>();
        groupUsers.add(groupUser1);
        groupUsers.add(groupUser2);

        doNothing().when(groupUserRepository).persist(groupUsers);

        Group response = groupService.createGroup(group, new String[]{user2.getUsername()}, user1.getId());

        assertEquals(group.getId(), response.getId());
        assertEquals(group.getName(), response.getName());
        verify(groupRepository, times(1)).persist(group);
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWithGroupNull() {
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(null, new String[]{}, user1.getId()));
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWithGroupNameNull() {
        group.setName(null);
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new String[]{}, user1.getId()));
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWithGroupNameEmpty() {
        group.setName("");
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new String[]{}, user1.getId()));
    }

    @Test
    void createGroupShouldThrowInvalidGroupExceptionWithGroupAlreadyExists() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new String[]{}, user1.getId()));
    }

    @Test
    void joinGroup() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(null);

        doNothing().when(groupUserRepository).persist(groupUser1);

        String expected = "request to join group has been submitted, waiting for one of the group creators to accept";
        String response = groupService.joinGroup(group.getName(), user1.getId());

        assertEquals(expected, response);
        verify(groupUserRepository, times(1)).persist(any(GroupUser.class));
    }

    @Test
    void joinGroupShouldThrowUnableToJoinGroupException() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId())).thenReturn(groupUser1);

        assertThrows(UnableToJoinGroupException.class, () -> groupService.joinGroup(group.getName(), user1.getId()));
    }

    @Test
    void leaveGroup() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser1);
        doNothing().when(groupUserRepository).delete(groupUser1);

        String response = groupService.leaveGroup(group.getName(), user1.getId());

        assertEquals("you left the group", response);
        verify(groupUserRepository, times(1)).delete(groupUser1);
    }

    @Test
    void acceptToGroup() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);

        when(userRepository.findByUsername(user1.getUsername())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser1);

        when(entityManager.merge(groupUser1)).thenReturn(groupUser1);

        String expected = "user has been accepted";
        String response = groupService.acceptToGroup(group.getName(), user2.getId(), user1.getUsername());

        assertEquals(expected, response);
        verify(entityManager, times(1)).merge(groupUser1);
    }

    @Test
    void acceptToGroupShouldThrowInvalidRoleException() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user3.getId()))
                .thenReturn(groupUser3);

        assertThrows(InvalidRoleException.class, () -> groupService.acceptToGroup(group.getName(), user3.getId(), user1.getUsername()));
    }

    @Test
    void acceptToGroupShouldThrowUnableToJoinGroupExceptionWithUserAlreadyMember() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);
        when(userRepository.findByUsername(user3.getUsername())).thenReturn(user3);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user3.getId()))
                .thenReturn(groupUser3);

        assertThrows(UnableToJoinGroupException.class, () -> groupService.acceptToGroup(group.getName(), user2.getId(), user3.getUsername()));
    }

    @Test
    void rejectFromEnteringGroup() {
        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);
        when(userRepository.findByUsername(user1.getName())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser1);
        doNothing().when(groupUserRepository).delete(groupUser1);

        String expected = "user has been rejected";
        String response = groupService.rejectFromEnteringGroup(group.getName(), user2.getId(), user1.getUsername());

        assertEquals(expected, response);
        verify(groupUserRepository, times(1)).delete(groupUser1);
    }

    @Test
    void getWaitingUsers() {
        List<GroupUser> users = new ArrayList<>();
        users.add(groupUser1);

        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);
        when(groupUserRepository.getWaitingUsers(group.getId())).thenReturn(users);

        List<String> response = groupService.getWaitingUsers(group.getName(), user2.getId());

        assertEquals(users.size(), response.size());
    }

    @Test
    void getUserJoinedGroups() {
        List<GroupUser> groups = new ArrayList<>();
        groups.add(groupUser2);
        when(groupUserRepository.getUserGroups(user2.getId()))
                .thenReturn(groups);

        List<String> response = groupService.getUserJoinedGroups(user2.getId());

        assertEquals(groups.size(), response.size());
    }

    @Test
    void getGroups() {
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        Group group2 = new Group();
        group2.setName("group2");
        groups.add(group2);

        when(groupRepository.getGroups("gr")).thenReturn(groups);

        List<String> response = groupService.getGroups("gr");

        assertEquals(groups.size(), response.size());
    }
}