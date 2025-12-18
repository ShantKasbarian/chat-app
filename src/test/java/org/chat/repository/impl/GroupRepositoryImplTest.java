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
    void getGroups() {
        List<Group> groups = groupRepository.getGroups(group.getName());
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
    }

    @Test
    void getUserGroups() {
        List<Group> groups = groupRepository.getUserGroups(user.getId());
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
    }
}
