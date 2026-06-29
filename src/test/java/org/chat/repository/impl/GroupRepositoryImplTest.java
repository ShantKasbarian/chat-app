package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GroupRepositoryImplTest {
    @Inject
    private GroupRepositoryImpl groupRepository;

    @Inject
    private UserRepositoryImpl userRepository;

    @Inject
    private GroupUserRepositoryImpl groupUserRepository;

    private GroupUser groupUser;

    private User user;

    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("company-chat");
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
    void tearDown() {
        groupUserRepository.delete(groupUser);
        userRepository.delete(user);
        groupRepository.delete(group);
    }

    @Test
    void existsByName() {
        assertTrue(groupRepository.existsByName(group.getName()));
    }

    @Test
    void findByName() {
        var groups = groupRepository.findByName(group.getName().substring(0, 1), 0, 10);

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
