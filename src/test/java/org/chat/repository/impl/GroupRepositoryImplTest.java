package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.entity.Group;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class GroupRepositoryImplTest {
    @Inject
    private GroupRepositoryImpl groupRepository;

    private Group group;

    @BeforeEach
    @Transactional
    void setUp() {
        group = new Group();
        group.setName("group");
        groupRepository.persist(group);
    }

    @AfterEach
    @Transactional
    void tearDown() {
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
}
