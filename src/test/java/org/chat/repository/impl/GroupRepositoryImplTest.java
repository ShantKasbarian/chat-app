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
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Testcontainers
class GroupRepositoryImplTest {
    @Container
    static MongoDBContainer mongo = MongoConfig.getContainer();

    @Inject
    private GroupRepositoryImpl groupRepository;

    @Inject
    private UserRepositoryImpl userRepository;

    @Inject
    private GroupUserRepositoryImpl groupUserRepository;

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
        groupUserRepository.delete(groupUser);
        userRepository.delete(user);
        groupRepository.delete(group);
    }

    @Test
    void existsById() {
        assertTrue(groupRepository.existsById(group.getId()));
    }

    @Test
    void existsByName() {
        assertTrue(groupRepository.existsByName(group.getName()));
    }

    @Test
    void findByName() {
        var groups = groupRepository.findByName(group.getName(), 0, 10);
        assertNotNull(groups);
        assertFalse(groups.list().isEmpty());
    }

    @Test
    void findByIds() {
        var groups = groupRepository.findByIds(List.of(group.getId()));
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
    }
}
