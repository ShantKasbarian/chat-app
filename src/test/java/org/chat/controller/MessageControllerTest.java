package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessageControllerTest {
    @InjectMocks
    private MessageController messageController;

    @Mock
    private MessageService messageService;

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private GroupMessageConverter groupMessageConverter;

    @Mock
    private UserContext userContext;

    @Mock
    private PanacheQuery<Message> panacheQuery;

    private User target;

    private Message message;

    private MessageDto messageDto;

    private Group group;

    private GroupMessageDto groupMessageDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        User sender = new User();
        sender.setId(UUID.randomUUID());
        sender.setUsername("sender");
        sender.setPassword("Password123+");

        target = new User();
        target.setId(UUID.randomUUID());
        target.setUsername("target");
        target.setPassword("Password123+");

        message = new Message();
        message.setId(UUID.randomUUID());
        message.setSenderId(sender.getId());
        message.setTargetUserId(target.getId());
        message.setText("some message");
        message.setTime(Instant.now());
        message.setGroupId(group.getId());

        messageDto = new MessageDto(message.getId(), sender.getId(), sender.getUsername(), target.getId(), target.getUsername(), message.getText(), message.getTime());
        groupMessageDto = new GroupMessageDto(message.getId(), sender.getId(), sender.getUsername(), message.getText(), group.getId(), message.getTime());
        UserPrincipal userPrincipal = new UserPrincipal(sender.getId(), sender.getUsername());

        when(userContext.get()).thenReturn(userPrincipal);
    }

    @Test
    void sendMessage() {
        when(messageService.sendMessage(any(UserPrincipal.class), anyString(), any(UUID.class)))
                .thenReturn(message);
        when(messageConverter.convertToModel(any(Message.class)))
                .thenReturn(messageDto);

        var response = messageController.sendMessage(messageDto);

        assertNotNull(response);
        assertEquals(messageDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(messageConverter).convertToModel(any(Message.class));
        verify(messageService).sendMessage(any(UserPrincipal.class), anyString(), any(UUID.class));
    }

    @Test
    void getMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageService.getMessages(any(UUID.class), any(UUID.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        when(messageConverter.convertToModel(any(Message.class))).thenReturn(messageDto);
        when(panacheQuery.list()).thenReturn(messages);
        when(panacheQuery.count()).thenReturn(10L);
        when(panacheQuery.pageCount()).thenReturn(1);

        var response = messageController.getMessages(target.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(messageConverter, times(messages.size())).convertToModel(any(Message.class));
        verify(messageService).getMessages(any(UUID.class), any(UUID.class), anyInt(), anyInt());
    }

    @Test
    void messageGroup() {
        when(messageService.messageGroup(any(UserPrincipal.class), anyString(), any(UUID.class)))
                .thenReturn(message);
        when(groupMessageConverter.convertToModel(any(Message.class)))
                .thenReturn(groupMessageDto);

        var response = messageController.messageGroup(groupMessageDto);

        assertNotNull(response);
        assertEquals(groupMessageDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(messageService).messageGroup(any(UserPrincipal.class), anyString(), any(UUID.class));
        verify(groupMessageConverter).convertToModel(any(Message.class));
    }

    @Test
    void getGroupMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        when(messageService.getGroupMessages(any(UUID.class), any(UUID.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        when(groupMessageConverter.convertToModel(any(Message.class)))
                .thenReturn(groupMessageDto);
        when(panacheQuery.list()).thenReturn(messages);
        when(panacheQuery.count()).thenReturn(10L);
        when(panacheQuery.pageCount()).thenReturn(1);

        var response = messageController.getGroupMessages(group.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(messageService).getGroupMessages(any(UUID.class), any(UUID.class), anyInt(), anyInt());
        verify(groupMessageConverter, times(messages.size())).convertToModel(any(Message.class));
    }
}