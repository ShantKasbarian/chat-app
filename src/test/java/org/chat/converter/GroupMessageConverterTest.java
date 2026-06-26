package org.chat.converter;

import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.GroupMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupMessageConverterTest {
    @InjectMocks
    private GroupMessageConverter groupMessageConverter;

    private Message message;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        message = new Message();
        message.setId(UUID.randomUUID());
        message.setSenderId(user.getId());
        message.setGroupId(group.getId());
        message.setText("some message");
        message.setTime(Instant.now());
    }

    @Test
    void convertToModel() {
        GroupMessageDto groupMessageDto = groupMessageConverter.convertToModel(message);

        assertNotNull(groupMessageDto);
        assertEquals(message.getId(), groupMessageDto.id());
        assertEquals(message.getSenderId(), groupMessageDto.senderId());
        assertEquals(message.getSenderUsernameSnapshot(), groupMessageDto.senderUsername());
        assertEquals(message.getText(), groupMessageDto.text());
        assertEquals(message.getGroupId(), groupMessageDto.groupId());
    }
}