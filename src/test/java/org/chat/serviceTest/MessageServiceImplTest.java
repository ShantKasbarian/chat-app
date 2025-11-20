package org.chat.serviceTest;

import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.InvalidInfoException;
import org.chat.exception.InvalidRoleException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.chat.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {
    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupUserRepository groupUserRepository;

    private User user1;

    private User user2;

    private Message message;

    private Group group;

    private GroupUser groupUser;

    private Message groupMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        message = new Message();
        message.setId(UUID.randomUUID());
        message.setRecipient(user1);
        message.setSender(user2);
        message.setText("some message");
        message.setTime(LocalDateTime.now());

        groupMessage = new Message();
        groupMessage.setId(UUID.randomUUID());
        groupMessage.setSender(user2);
        groupMessage.setText("some message");
        groupMessage.setTime(LocalDateTime.now());
        groupMessage.setGroup(group);

        groupUser = new GroupUser(UUID.randomUUID(), group, user2, false, true);
    }

    @Test
    void sendMessage() {
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(userRepository.findById(user2.getId())).thenReturn(user2);
        doNothing().when(messageRepository).persist(message);

        Message response = messageService.sendMessage(message.getText(), user1.getId(), user2.getId());

        assertEquals(message.getText(), response.getText());
        assertEquals(message.getSender().getId(), response.getSender().getId());
        assertEquals(message.getRecipient().getId(), response.getRecipient().getId());
        assertNull(response.getGroup());
        verify(messageRepository).persist(any(Message.class));
    }

    @Test
    void sendMessageShouldThrowInvalidInfoExceptionWhenMessageIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.sendMessage(null, user2.getId(),user1.getId()));
    }

    @Test
    void sendMessageShouldThrowInvalidInfoExceptionWhenMessageIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.sendMessage("", user2.getId(),user1.getId()));
    }

    @Test
    void sendMessageShouldThrowInvalidInfoExceptionWhenRecipientIdIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.sendMessage(message.getText(), null, user1.getId()));
    }

    @Test
    void getMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageRepository.getMessages(any(UUID.class), any(UUID.class), anyInt(), anyInt()))
                .thenReturn(messages);

        var response = messageService.getMessages(user2.getId(), user1.getId(), 0, 10);

        assertEquals(messages.size(), response.size());
    }

    @Test
    void messageGroup() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser);
        when(userRepository.findById(any(UUID.class))).thenReturn(user2);
        doNothing().when(messageRepository).persist(any(Message.class));

        Message response = messageService.messageGroup(groupMessage.getText(), group.getId(), user2.getId());

        assertEquals(groupMessage.getText(), response.getText());
        assertEquals(groupMessage.getSender().getId(), response.getSender().getId());
        assertEquals(groupMessage.getGroup().getId(), response.getGroup().getId());
        assertNull(response.getRecipient());
        verify(messageRepository).persist(any(Message.class));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenMessageIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup(null, groupMessage.getGroup().getId(), user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenMessageIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup("", groupMessage.getGroup().getId(), user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenGroupIdIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup(groupMessage.getText(), null, user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidRoleExceptionWhenGroupUserIsNull() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);

        assertThrows(InvalidRoleException.class, () -> messageService.messageGroup("some message", group.getId(), user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidRoleExceptionWhenGroupUserRoleIsNotMember() {
        groupUser.setIsMember(false);

        when(groupRepository.findById(any(UUID.class))).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser);

        assertThrows(InvalidRoleException.class, () -> messageService.messageGroup("some message", group.getId(), user1.getId()));
    }

    @Test
    void getGroupMessages() {
        groupUser.setIsMember(true);

        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(groupRepository.existsById(group.getId())).thenReturn(true);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser);
        when(messageRepository.getGroupMessages(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(messages);

        var response = messageService.getGroupMessages(group.getId(), user2.getId(), 0, 10);

        assertEquals(messages.size(), response.size());
    }

    @Test
    void getGroupMessagesShouldThrowInvalidRoleExceptionWhenGroupUserIsNotMember() {
        groupUser.setIsCreator(false);
        groupUser.setIsMember(false);

        when(groupRepository.existsById(any(UUID.class))).thenReturn(true);
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(groupUser);

        assertThrows(InvalidRoleException.class, () -> messageService.getGroupMessages(group.getId(), user1.getId(), 0, 10));
    }

    @Test
    void getGroupMessagesShouldThrowInvalidInfoExceptionWhenGroupIdIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.getGroupMessages(null, user1.getId(), 0, 10));
    }
}