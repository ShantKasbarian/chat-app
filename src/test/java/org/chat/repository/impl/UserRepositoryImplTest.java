package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.entity.User;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserRepositoryImplTest {
    @Inject
    private UserRepositoryImpl userRepository;

    private User user;

    @BeforeEach
    @Transactional
    void setUp() {
        user = new User();
        user.setUsername("user");
        user.setPassword("Password123+");
        userRepository.persist(user);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userRepository.delete(user);
    }

    @Test
    void findByUsername() {
        User user = userRepository.findById(this.user.getId());
        assertNotNull(user);
        assertEquals(this.user.getId(), user.getId());
    }

    @Test
    void existsByUsername() {
        assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    void searchByUsername() {
        List<User> users = userRepository.searchByUsername(user.getUsername());
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
}