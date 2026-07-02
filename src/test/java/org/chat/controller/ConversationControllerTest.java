package org.chat.controller;

import jakarta.ws.rs.core.Response;
import org.chat.converter.ConversationConverter;
import org.chat.entity.Message;
import org.chat.model.ConversationDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ConversationControllerTest {
    @InjectMocks
    private ConversationController conversationController;

    @Mock
    private MessageService messageService;

    @Mock
    private ConversationConverter conversationConverter;

    @Mock
    private UserContext userContext;

    private UserPrincipal userPrincipal;

    private PageDto<Message> pageDto;

    private ConversationDto conversationDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userPrincipal = new UserPrincipal(UUID.randomUUID(), "John");
        pageDto = new PageDto<>(List.of(new Message()), 1, 1);
        conversationDto = new ConversationDto(UUID.randomUUID(), "Jack", null, "hi", Message.Type.USER, Instant.now());
    }

    @Test
    void getConversations() {
        when(userContext.get()).thenReturn(userPrincipal);
        when(messageService.findLatestByUserId(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(pageDto);
        when(conversationConverter.convertToModel(any(Message.class), any(UUID.class)))
                .thenReturn(conversationDto);

        Response response = conversationController.getConversations(0, 10);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        verify(userContext).get();
        verify(messageService).findLatestByUserId(any(UUID.class), anyInt(), anyInt());
        verify(conversationConverter, atLeast(1)).convertToModel(any(Message.class), any(UUID.class));
    }
}