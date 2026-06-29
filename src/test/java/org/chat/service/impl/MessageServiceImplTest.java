package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.ResourceNotFoundException;
import org.chat.exception.UnauthorizedException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.chat.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.chat.service.impl.GroupServiceImpl.REQUEST_NOT_AUTHORIZED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {
    private static final String USER_NOT_FOUND = "user not found";

    private static final String GROUP_NOT_FOUND_MESSAGE = "group not found";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you are not a member of this group";

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

    @Mock
    private PanacheQuery<Message> panacheQuery;

    private User user1;

    private User user2;

    private Message message;

    private Group group;

    private GroupUser groupUser;

    private Message groupMessage;

    private UserPrincipal userPrincipal;

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
        message.setSenderId(user1.getId());
        message.setTargetUserId(user2.getId());
        message.setText("some message");
        message.setTime(Instant.now());

        groupMessage = new Message();
        groupMessage.setId(UUID.randomUUID());
        groupMessage.setSenderId(user2.getId());
        groupMessage.setText("some message");
        groupMessage.setTime(Instant.now());
        groupMessage.setGroupId(group.getId());

        groupUser = new GroupUser(UUID.randomUUID(), group.getId(), user1.getId(), user1.getUsername(), GroupUser.Role.MEMBER);
        userPrincipal = new UserPrincipal(UUID.randomUUID(), user1.getUsername());
    }

    @Test
    void sendMessage() {
        when(userRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(user2));
        doNothing().when(messageRepository).persist(message);

        Message response = messageService.sendMessage(userPrincipal, message.getText(), user1.getId());

        assertNotNull(response);
        verify(userRepository).findByIdOptional(any(UUID.class));
        verify(messageRepository).persist(any(Message.class));
    }

    @Test
    void sendMessageShouldThrowResourceNotFoundExceptionWhenTargetUserNotFound() {
        when(userRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> messageService.sendMessage(userPrincipal, message.getText(),user1.getId()));
        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userRepository).findByIdOptional(any(UUID.class));
    }

    @Test
    void getMessages() {
        when(messageRepository.findByUserIdTargetUserId(any(UUID.class), any(UUID.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);

        var response = messageService.getMessages(user2.getId(), user1.getId(), 0, 10);

        assertNotNull(response);
        verify(messageRepository).findByUserIdTargetUserId(any(UUID.class), any(UUID.class), anyInt(), anyInt());
    }

    @Test
    void messageGroup() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.ofNullable(groupUser));
        doNothing().when(messageRepository).persist(any(Message.class));

        Message response = messageService.messageGroup(userPrincipal, groupMessage.getText(), group.getId());

        assertNotNull(response);
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(messageRepository).persist(any(Message.class));
    }

    @Test
    void messageGroupShouldThrowResourceNotFoundExceptionWhenGroupUserNotFound() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> messageService.messageGroup(userPrincipal, groupMessage.getText(), groupMessage.getGroupId()));
        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
    }

    @Test
    void messageGroupShouldThrowUnauthorizedExceptionWhenRoleIsPending() {
        groupUser.setRole(GroupUser.Role.PENDING);

        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser));

        Exception exception = assertThrows(UnauthorizedException.class, () -> messageService.messageGroup(userPrincipal, groupMessage.getText(), groupMessage.getGroupId()));
        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
    }

    @Test
    void getGroupMessages() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser));
        when(messageRepository.findByGroupId(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);

        var response = messageService.getGroupMessages(group.getId(), user2.getId(), 0, 10);

        assertNotNull(response);
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
        verify(messageRepository).findByGroupId(any(UUID.class), anyInt(), anyInt());
    }

    @Test
    void getGroupMessagesShouldThrowResourceNotFoundExceptionWhenGroupUserNotFound() {
        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> messageService.getGroupMessages(group.getId(), user1.getId(), 0, 10));
        assertEquals(NOT_MEMBER_OF_GROUP_MESSAGE, exception.getMessage());
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void getGroupMessagesShouldThrowUnauthorizedExceptionWhenGroupUserRoleIsPending() {
        groupUser.setRole(GroupUser.Role.PENDING);

        when(groupUserRepository.findByGroupIdUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(groupUser));

        Exception exception = assertThrows(UnauthorizedException.class, () -> messageService.getGroupMessages(group.getId(), user1.getId(), 0, 10));
        assertEquals(REQUEST_NOT_AUTHORIZED, exception.getMessage());
        verify(groupUserRepository).findByGroupIdUserId(any(UUID.class), any(UUID.class));
    }
}
