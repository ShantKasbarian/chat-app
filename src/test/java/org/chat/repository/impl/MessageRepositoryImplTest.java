package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.repository.GroupRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MessageRepositoryImplTest {
    @Inject
    private MessageRepositoryImpl messageRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private GroupRepository groupRepository;

    private User user;

    private User target;

    private Message message;

    private Message groupMessage;

    private Group group;

    @BeforeEach
    @Transactional
    void setUp() {
        user = new User();
        user.setUsername("user");
        user.setPassword("Password123+");
        userRepository.persist(user);

        target = new User();
        target.setUsername("target");
        target.setPassword("Password123+");
        userRepository.persist(target);

        message = new Message();
        message.setSender(user);
        message.setTarget(target);
        message.setText("some message");
        message.setTime(LocalDateTime.now());
        messageRepository.persist(message);

        group = new Group();
        group.setName("group");
        groupRepository.persist(group);

        groupMessage = new Message();
        groupMessage.setSender(user);
        groupMessage.setGroup(group);
        groupMessage.setText("some message");
        groupMessage.setTime(LocalDateTime.now());
        messageRepository.persist(groupMessage);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        messageRepository.delete(message);
        messageRepository.delete(groupMessage);
        groupRepository.delete(group);
        userRepository.delete(user);
        userRepository.delete(target);
    }

    @Test
    void getMessages() {
        List<Message> messages = messageRepository.getMessages(user.getId(), target.getId(), 1, 10);
        assertNotNull(messages);
        assertFalse(messages.isEmpty());
    }

    @Test
    void getGroupMessages() {
        List<Message> messages = messageRepository.getGroupMessages(group.getId(), 1, 10);
        assertNotNull(messages);
        assertFalse(messages.isEmpty());
    }
}