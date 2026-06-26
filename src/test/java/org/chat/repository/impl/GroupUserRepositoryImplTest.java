package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.config.MongoConfig;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GroupUserRepositoryImplTest {
    @Container
    static MongoDBContainer mongo = MongoConfig.getContainer();

    @Inject
    private GroupUserRepositoryImpl groupUserRepository;

    @Inject
    private UserRepositoryImpl userRepository;

    @Inject
    private GroupRepositoryImpl groupRepository;

    private GroupUser groupUser;

    private User user;

    private Group group;

    static {
        mongo.start();
    }

    @BeforeEach
    @Transactional
    void setUp() {
        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");
        groupRepository.persist(group);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");
        userRepository.persist(user);

        groupUser = new GroupUser();
        groupUser.setId(UUID.randomUUID());
        groupUser.setGroupId(group.getId());
        groupUser.setUserId(user.getId());
        groupUser.setRole(GroupUser.Role.ADMIN);

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
        Optional<GroupUser> groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), user.getId());

        assertNotNull(groupUser);
        assertTrue(groupUser.isPresent());
        assertEquals(group.getId(), groupUser.get().getGroupId());
        assertEquals(user.getId(), groupUser.get().getUserId());
    }

    @Test
    void existsByGroupIdUserId() {
        assertTrue(groupUserRepository.existsByGroupIdUserId(group.getId(), user.getId()));
    }

    @Test
    void findByRole() {
        var groupUsers = groupUserRepository.findByRole(group.getId(), GroupUser.Role.ADMIN, 0, 10);

        assertNotNull(groupUsers);
        assertFalse(groupUsers.list().isEmpty());
    }

    @Test
    void findByUserId() {
        var groupUsers = groupUserRepository.findByUserId(user.getId(), 0, 10);

        assertNotNull(groupUsers);
        assertFalse(groupUsers.list().isEmpty());
    }
}