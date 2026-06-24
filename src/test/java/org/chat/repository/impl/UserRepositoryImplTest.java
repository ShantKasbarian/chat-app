package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.config.MongoConfig;
import org.chat.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Testcontainers
class UserRepositoryImplTest {
    @Container
    static MongoDBContainer mongo = MongoConfig.getContainer();

    @Inject
    private UserRepositoryImpl userRepository;

    private User user;

    static {
        mongo.start();
    }

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
    void findByUsernamePage() {
        var users = userRepository.findByUsername(user.getUsername(), 0, 10);

        assertNotNull(users);
        assertFalse(users.list().isEmpty());
    }
}