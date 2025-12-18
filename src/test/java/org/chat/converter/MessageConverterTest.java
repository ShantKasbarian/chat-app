package org.chat.converter;

import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.MessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageConverterTest {
    @InjectMocks
    private MessageConverter messageConverter;

    private Message message;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");

        message = new Message();
        message.setId(UUID.randomUUID());
        message.setTarget(user1);
        message.setSender(user2);
        message.setText("some message");
        message.setTime(LocalDateTime.now());
    }

    @Test
    void convertToModel() {
        MessageDto messageDto = messageConverter.convertToModel(message);

        assertNotNull(messageDto);
        assertEquals(message.getId(), messageDto.id());
        assertEquals(message.getSender().getId(), messageDto.senderId());
        assertEquals(message.getSender().getUsername(), messageDto.senderUsername());
        assertEquals(message.getTarget().getId(), messageDto.targetUserId());
        assertEquals(message.getTarget().getUsername(), messageDto.targetUsername());
        assertEquals(message.getText(), messageDto.text());
    }
}