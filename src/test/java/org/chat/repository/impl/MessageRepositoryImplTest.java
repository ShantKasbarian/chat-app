package org.chat.repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.UUID;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.repository.GroupRepository;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class MessageRepositoryImplTest {
  @Inject private MessageRepositoryImpl messageRepository;

  @Inject private UserRepository userRepository;

  @Inject private GroupRepository groupRepository;

  private User user;

  private User target;

  private Message message;

  private Message groupMessage;

  private Group group;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("user");
    user.setPassword("Password123+");
    userRepository.persist(user);

    target = new User();
    target.setId(UUID.randomUUID());
    target.setUsername("target");
    target.setPassword("Password123+");
    userRepository.persist(target);

    message = new Message();
    message.setId(UUID.randomUUID());
    message.setSenderId(user.getId());
    message.setTargetUserId(target.getId());
    message.setText("some message");
    message.setTime(Instant.now());
    messageRepository.persist(message);

    group = new Group();
    group.setId(UUID.randomUUID());
    group.setName("group");
    groupRepository.persist(group);

    groupMessage = new Message();
    groupMessage.setId(UUID.randomUUID());
    groupMessage.setSenderId(user.getId());
    groupMessage.setSenderUsernameSnapshot(user.getUsername());
    groupMessage.setGroupId(group.getId());
    groupMessage.setText("some message");
    groupMessage.setTime(Instant.now());
    messageRepository.persist(groupMessage);
  }

  @AfterEach
  void tearDown() {
    messageRepository.delete(message);
    messageRepository.delete(groupMessage);
    groupRepository.delete(group);
    userRepository.delete(user);
    userRepository.delete(target);
  }

  @Test
  void findByUserIdTargetUserId() {
    var messages = messageRepository.findByUserIdTargetUserId(user.getId(), target.getId(), 0, 10);

    assertNotNull(messages);
    assertFalse(messages.list().isEmpty());
  }

  @Test
  void findByGroupId() {
    var messages = messageRepository.findByGroupId(group.getId(), 0, 10);

    assertNotNull(messages);
    assertFalse(messages.list().isEmpty());
  }

  @Test
  void findLatestByUserId() {
    var messages = messageRepository.findLatestByUserId(user.getId(), 0, 10);

    assertNotNull(messages);
    assertFalse(messages.content().isEmpty());
    assertTrue(messages.totalElements() >= 1);
  }
}
