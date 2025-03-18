package org.chat.serviceTests;

import org.chat.converters.GroupMessageConverter;
import org.chat.converters.MessageConverter;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.Message;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.exceptions.InvalidRoleException;
import org.chat.models.GroupMessageDto;
import org.chat.models.MessageDto;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.MessageRepository;
import org.chat.repositories.UserRepository;
import org.chat.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupUserRepository groupUserRepository;

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
    void writeMessage() {
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(user1);
        when(userRepository.findById(user2.getId())).thenReturn(user2);

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setRecipient(user1);
        message.setSender(user2);
        message.setMessage("some message");
        message.setTime(LocalDateTime.now());
        doNothing().when(messageRepository).persist(message);

        Message response = messageService.writeMessage(message.getMessage(), user1.getUsername(), user2.getId());

        assertEquals(message.getMessage(), response.getMessage());
        assertEquals(message.getSender().getId(), response.getSender().getId());
        assertEquals(message.getRecipient().getId(), response.getRecipient().getId());
        assertNull(message.getGroup());
        verify(messageRepository, times(1)).persist(any(Message.class));
    }

    @Test
    void writeMessageShouldThrowInvalidInfoExceptionWhenMessageIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.writeMessage(null, user2.getUsername(),user1.getId()));
    }

    @Test
    void writeMessageShouldThrowInvalidInfoExceptionWhenMessageIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.writeMessage("", user2.getUsername(),user1.getId()));
    }

    @Test
    void writeMessageShouldThrowInvalidInfoExceptionWhenUsernameIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.writeMessage("some message", null, user1.getId()));
    }

    @Test
    void writeMessageShouldThrowInvalidInfoExceptionWhenUsernameIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.writeMessage("some message", "", user1.getId()));
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

        when(userRepository.findByUsername(user1.getUsername())).thenReturn(user1);
        when(messageRepository.getMessages(user2.getId(), user1.getId()))
                .thenReturn(messages);
        when(messageConverter.convertToModel(message1)).thenReturn(messageDto1);
        when(messageConverter.convertToModel(message2)).thenReturn(messageDto2);

        List<MessageDto> response = messageService.getMessages(user2.getId(), user1.getUsername());

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

        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId())).thenReturn(groupUser);
        when(userRepository.findById(user2.getId())).thenReturn(user2);
        doNothing().when(messageRepository).persist(message);

        Message response = messageService.messageGroup(message.getMessage(), group.getName(), user2.getId());

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
    void messageGroupShouldThrowInvalidInfoExceptionWhenGroupNameIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup("some message", "", user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidInfoExceptionWhenGroupNameIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.messageGroup("some message", null, user1.getId()));
    }

    @Test
    void messageGroupShouldThrowInvalidRoleExceptionWhenGroupUserIsNull() {
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName("group");

        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(null);

        assertThrows(InvalidRoleException.class, () -> messageService.messageGroup("some message", "group", user1.getId()));
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

        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser);

        assertThrows(InvalidRoleException.class, () -> messageService.messageGroup("some message", "group", user1.getId()));
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

        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user2.getId()))
                .thenReturn(
                        new GroupUser(
                                UUID.randomUUID().toString(),
                                group, user2,
                                true,
                                true
                        )
                );
        when(messageRepository.getGroupMessages(group.getId())).thenReturn(messages);
        when(groupMessageConverter.convertToModel(message1)).thenReturn(messageDto1);
        when(groupMessageConverter.convertToModel(message2)).thenReturn(messageDto2);

        List<GroupMessageDto> response = messageService.getGroupMessages(group.getName(), user2.getId());

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

        when(groupRepository.findByName(group.getName())).thenReturn(group);
        when(groupUserRepository.findByGroupIdUserId(group.getId(), user1.getId()))
                .thenReturn(groupUser);

        assertThrows(InvalidRoleException.class, () -> messageService.getGroupMessages(group.getName(), user1.getId()));
    }

    @Test
    void getGroupMessagesShouldThrowInvalidInfoExceptionWhenGroupNameIsNull() {
        assertThrows(InvalidInfoException.class, () -> messageService.getGroupMessages(null, user1.getId()));
    }

    @Test
    void getGroupMessagesShouldThrowInvalidInfoExceptionWhenGroupNameIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> messageService.getGroupMessages("", user1.getId()));
    }
}