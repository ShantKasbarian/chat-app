package org.chat.serviceTest;

import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.InvalidInfoException;
import org.chat.exception.InvalidRoleException;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.repository.impl.GroupRepositoryImpl;
import org.chat.repository.impl.GroupUserRepositoryImpl;
import org.chat.repository.impl.MessageRepositoryImpl;
import org.chat.repository.impl.UserRepositoryImpl;
import org.chat.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {
    @InjectMocks
    private MessageServiceImpl messageService;

    @Mock
    private MessageRepositoryImpl messageRepository;

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private GroupRepositoryImpl groupRepository;

    @Mock
    private GroupUserRepositoryImpl groupUserRepository;

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private GroupMessageConverter groupMessageConverter;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setUsername("user2");
    }

    @Test
    void sendMessage() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setRecipient(user1);
        message.setSender(user2);
        message.setMessage("some message");
        message.setTime(LocalDateTime.now());
        doNothing().when(messageRepository).persist(message);

        Message response = messageService.sendMessage(message.getMessage(), user1.getId(), user2.getId());

        assertEquals(message.getMessage(), response.getMessage());
        assertEquals(message.getSender().getId(), response.getSender().getId());
        assertEquals(message.getRecipient().getId(), response.getRecipient().getId());
        assertNull(message.getGroup());
        verify(messageRepository, times(1)).persist(any(Message.class));
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
        assertThrows(InvalidInfoException.class, () -> messageService.sendMessage("some message", null, user1.getId()));
    }

    @Test
    void sendMessageShouldThrowInvalidInfoExceptionWhenRecipientIdIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.sendMessage("some message", "", user1.getId()));
    }

    @Test
    void getMessages() {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(UUID.randomUUID().toString());
        message1.setRecipient(user1);
        message1.setSender(user2);
        message1.setMessage("some message");
        message1.setTime(LocalDateTime.now());

        Message message2 = new Message();
        message2.setId(UUID.randomUUID().toString());
        message2.setRecipient(user2);
        message2.setSender(user1);
        message2.setMessage("some message");
        message2.setTime(LocalDateTime.now());

        messages.add(message1);
        messages.add(message2);

        MessageDto messageDto1 = new MessageDto(
                message1.getId(),
                message1.getSender().getId(),
                message1.getSender().getUsername(),
                message1.getRecipient().getId(),
                message1.getRecipient().getUsername(),
                message1.getMessage(),
                message1.getTime().toString()
        );


        MessageDto messageDto2 = new MessageDto(
                message2.getId(),
                message2.getSender().getId(),
                message2.getSender().getUsername(),
                message2.getRecipient().getId(),
                message2.getRecipient().getUsername(),
                message2.getMessage(),
                message2.getTime().toString()
        );

        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        when(messageRepository.getMessages(user2.getId(), user1.getId(), 1, 10))
                .thenReturn(messages);
        when(messageConverter.convertToModel(message1)).thenReturn(messageDto1);
        when(messageConverter.convertToModel(message2)).thenReturn(messageDto2);

        List<MessageDto> response = messageService.getMessages(user2.getId(), user1.getId(), 0, 10);

        assertEquals(messages.size(), response.size());
    }

    @Test
    void messageGroup() {
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSender(user2);
        message.setGroup(group);
        message.setMessage("some message");
        message.setTime(LocalDateTime.now());

        GroupUser groupUser = new GroupUser(UUID.randomUUID().toString(), group, user2, false, true);

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId())).thenReturn(groupUser);
        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
        doNothing().when(messageRepository).persist(message);

        Message response = messageService.messageGroup(message.getMessage(), group.getId(), user2.getId());

        assertEquals(message.getMessage(), response.getMessage());
        assertEquals(message.getSender().getId(), response.getSender().getId());
        assertEquals(message.getGroup().getId(), response.getGroup().getId());
        assertNull(message.getRecipient());
        verify(messageRepository, times(1)).persist(any(Message.class));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenMessageIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup(null, "group", user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenMessageIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup("", "group", user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenGroupIdIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup("some message", "", user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenGroupIdIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup("some message", null, user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidRoleExceptionWhenGroupUserIsNull() {
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(null);

        assertThrows(InvalidRoleException.class, () -> messageService.messageGroup("some message", group.getId(), user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidRoleExceptionWhenGroupUserRoleIsNotMember() {
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        GroupUser groupUser = new GroupUser();
        groupUser.setId(UUID.randomUUID().toString());
        groupUser.setGroup(group);
        groupUser.setUser(user1);
        groupUser.setIsCreator(false);
        groupUser.setIsMember(false);

        when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser);

        assertThrows(InvalidRoleException.class, () -> messageService.messageGroup("some message", group.getId(), user1.getId()));
    }

    @Test
    void getGroupMessages() {
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        message1.setId(UUID.randomUUID().toString());
        message1.setGroup(group);
        message1.setSender(user2);
        message1.setMessage("some message");
        message1.setTime(LocalDateTime.now());

        Message message2 = new Message();
        message2.setId(UUID.randomUUID().toString());
        message2.setGroup(group);
        message2.setSender(user1);
        message2.setMessage("some message");
        message2.setTime(LocalDateTime.now());

        messages.add(message1);
        messages.add(message2);

        GroupMessageDto messageDto1 = new GroupMessageDto(
                message1.getId(),
                message1.getSender().getId(),
                message1.getSender().getUsername(),
                message1.getGroup().getId(),
                message1.getGroup().getName(),
                message1.getMessage(),
                message1.getTime().toString()
        );


        GroupMessageDto messageDto2 = new GroupMessageDto(
                message2.getId(),
                message2.getSender().getId(),
                message2.getSender().getUsername(),
                message2.getGroup().getId(),
                message2.getGroup().getName(),
                message2.getMessage(),
                message2.getTime().toString()
        );

        when(groupRepository.existsById(group.getId())).thenReturn(true);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(
                        new GroupUser(
                                UUID.randomUUID().toString(),
                                group, user2,
                                true,
                                true
                        )
                );
        when(messageRepository.getGroupMessages(group.getId(), 1, 2)).thenReturn(messages);
        when(groupMessageConverter.convertToModel(message1)).thenReturn(messageDto1);
        when(groupMessageConverter.convertToModel(message2)).thenReturn(messageDto2);

        List<GroupMessageDto> response = messageService.getGroupMessages(group.getId(), user2.getId(), 0, 2);

        assertEquals(messages.size(), response.size());
    }

    @Test
    void getGroupMessagesShouldThrowInvalidRoleExceptionWhenGroupUserIsNotMember() {
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        GroupUser groupUser = new GroupUser();
        groupUser.setId(UUID.randomUUID().toString());
        groupUser.setGroup(group);
        groupUser.setUser(user1);
        groupUser.setIsCreator(false);
        groupUser.setIsMember(false);

        when(groupRepository.existsById(group.getId())).thenReturn(true);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser);

        assertThrows(InvalidRoleException.class, () -> messageService.getGroupMessages(group.getId(), user1.getId(), 0, 10));
    }

    @Test
    void getGroupMessagesShouldThrowInvalidInfoExceptionWhenGroupIdIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.getGroupMessages(null, user1.getId(), 0, 10));
    }

    @Test
    void getGroupMessagesShouldThrowInvalidInfoExceptionWhenGroupIdIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.getGroupMessages("", user1.getId(), 0, 10));
    }
}