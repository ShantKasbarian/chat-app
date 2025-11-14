package org.chat.serviceTest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
import org.chat.service.GroupService;
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

    @Mock
    private PanacheQuery<Group> mockQuery;

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

        when(groupRepository.find(anyString(), anyString())).thenReturn(mockQuery);
    }

    @Test
    void createGroup() {
        when(groupRepository.find("name", group.getName()).firstResult()).thenReturn(null);
        doNothing().when(groupRepository).persist(group);

        when(userRepository.findById(user1.getId())).thenReturn(user1);

        when(userRepository.findById(user2.getId())).thenReturn(user2);

        groupUser1.setIsCreator(true);
        groupUser1.setIsMember(true);

        List<GroupUser> groupUsers = new ArrayList<>();
        groupUsers.add(groupUser1);
        groupUsers.add(groupUser2);

        doNothing().when(groupUserRepository).persist(groupUsers);

        Group response = groupService.createGroup(group, new String[]{user2.getId()}, user1.getId());

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
        when(groupRepository.find("name", group.getName()).firstResult())
        .thenReturn(group);
        assertThrows(InvalidGroupException.class, () -> groupService.createGroup(group, new String[]{}, user1.getId()));
    }

    @Test
    void joinGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(null);

        doNothing().when(groupUserRepository).persist(groupUser1);

        GroupUser response = groupService.joinGroup(group.getId(), user1.getId());

        assertNotNull(response);
        assertEquals(group.getId(), response.getGroup().getId());
        assertEquals(user1.getId(), response.getUser().getId());
        verify(groupUserRepository, times(1)).persist(any(GroupUser.class));
    }

    @Test
    void joinGroupShouldThrowUnableToJoinGroupException() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId())).thenReturn(groupUser1);

        assertThrows(UnableToJoinGroupException.class, () -> groupService.joinGroup(group.getId(), user1.getId()));
    }

    @Test
    void leaveGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser1);
        doNothing().when(groupUserRepository).delete(groupUser1);

        String response = groupService.leaveGroup(group.getId(), user1.getId());

        assertEquals("you left the group", response);
        verify(groupUserRepository, times(1)).delete(groupUser1);
    }

    @Test
    void acceptToGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);

        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser1);

        when(entityManager.merge(groupUser1)).thenReturn(groupUser1);

        GroupUser response = groupService.acceptToGroup(group.getId(), user2.getId(), user1.getId());

        assertNotNull(response);
        assertEquals(groupUser1.getId(), response.getId());
        verify(entityManager, times(1)).merge(groupUser1);
    }

    @Test
    void acceptToGroupShouldThrowInvalidRoleException() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user3.getId()))
                .thenReturn(groupUser3);

        assertThrows(InvalidRoleException.class, () -> groupService.acceptToGroup(group.getId(), user3.getId(), user1.getId()));
    }

    @Test
    void acceptToGroupShouldThrowUnableToJoinGroupExceptionWithUserAlreadyMember() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);
        when(userRepository.findById(user3.getId())).thenReturn(user3);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user3.getId()))
                .thenReturn(groupUser3);

        assertThrows(UnableToJoinGroupException.class, () -> groupService.acceptToGroup(group.getId(), user2.getId(), user3.getId()));
    }

    @Test
    void rejectFromEnteringGroup() {
        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser1);
        doNothing().when(groupUserRepository).delete(groupUser1);

        String expected = "user has been rejected";
        String response = groupService.rejectFromEnteringGroup(group.getId(), user2.getId(), user1.getId());

        assertEquals(expected, response);
        verify(groupUserRepository, times(1)).delete(groupUser1);
    }

    @Test
    void getWaitingUsers() {
        List<GroupUser> users = new ArrayList<>();
        users.add(groupUser1);

        when(groupRepository.findById(group.getId())).thenReturn(Optional.ofNullable(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(groupUser2);
        when(groupUserRepository.getWaitingUsers(group.getId())).thenReturn(users);

        List<GroupUser> response = groupService.getWaitingUsers(group.getId(), user2.getId());

        assertNotNull(response);
        assertEquals(users.size(), response.size());
    }

    @Test
    void getUserJoinedGroups() {
        List<GroupUser> groups = new ArrayList<>();
        groups.add(groupUser2);
        when(groupUserRepository.getUserGroups(user2.getId()))
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

        when(groupRepository.getGroups("gr")).thenReturn(groups);

        List<Group> response = groupService.getGroups("gr");

        assertNotNull(response);
        assertEquals(groups.size(), response.size());
    }
}