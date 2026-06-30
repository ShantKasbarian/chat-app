package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.chat.entity.Group;
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

    private Group group;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("company-chat");
        groupRepository.persist(group);
    }

    @AfterEach
    void tearDown() {
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
