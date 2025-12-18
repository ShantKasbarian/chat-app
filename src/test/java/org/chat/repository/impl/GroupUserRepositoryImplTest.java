package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GroupUserRepositoryImplTest {
    @Inject
    private GroupUserRepositoryImpl groupUserRepository;

    @Inject
    private UserRepositoryImpl userRepository;

    @Inject
    private GroupRepositoryImpl groupRepository;

    private GroupUser groupUser;

    private User user;

    private Group group;

    @BeforeEach
    @Transactional
    void setUp() {
        group = new Group();
        group.setName("group");
        groupRepository.persist(group);

        user = new User();
        user.setUsername("user");
        user.setPassword("Password123+");
        userRepository.persist(user);

        groupUser = new GroupUser();
        groupUser.setGroup(group);
        groupUser.setUser(user);
        groupUser.setIsMember(true);
        groupUser.setIsCreator(false);

        groupUserRepository.persist(groupUser);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        groupRepository.delete(group);
        userRepository.delete(user);
        groupUserRepository.delete(groupUser);
    }

    @Test
    void findByGroupIdUserId() {
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), user.getId());

        assertNotNull(groupUser);
        assertEquals(group.getId(), groupUser.getGroup().getId());
        assertEquals(user.getId(), groupUser.getUser().getId());
    }

    @Test
    void existsByGroupIdUserId() {
        assertTrue(groupUserRepository.existsByGroupIdUserId(group.getId(), user.getId()));
    }

    @Test
    @Transactional
    void getWaitingUsers() {
        User waitingUser = new User();
        waitingUser.setUsername("waitingUser");
        waitingUser.setPassword("Password123+");
        userRepository.persist(waitingUser);

        Group group = new Group();
        group.setName("group1");
        groupRepository.persist(group);

        GroupUser waitingGroupUser = new GroupUser();
        waitingGroupUser.setGroup(group);
        waitingGroupUser.setUser(waitingUser);
        waitingGroupUser.setIsMember(false);
        waitingGroupUser.setIsCreator(false);
        groupUserRepository.persist(waitingGroupUser);

        List<GroupUser> groupUsers = groupUserRepository.getWaitingUsers(group.getId());

        assertNotNull(groupUsers);
        assertFalse(groupUsers.isEmpty());
    }
}