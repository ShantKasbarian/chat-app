package org.chat.repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.chat.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserRepositoryImplTest {
  @Inject private UserRepositoryImpl userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("user");
    user.setPassword("Password123+");
    userRepository.persist(user);
  }

  @AfterEach
  void tearDown() {
    userRepository.delete(user);
  }

  @Test
  void findByUsername() {
    User user = userRepository.findByUsername(this.user.getUsername()).orElse(null);

    assertNotNull(user);
    assertEquals(this.user.getUsername(), user.getUsername());
  }

  @Test
  void findByUsernamePage() {
    var users = userRepository.findByUsername(user.getUsername().substring(0, 1), 0, 10);

    assertNotNull(users);
    assertFalse(users.list().isEmpty());
  }

  @Test
  void existsByUsername() {
    assertTrue(userRepository.existsByUsername(user.getUsername()));
  }
}
