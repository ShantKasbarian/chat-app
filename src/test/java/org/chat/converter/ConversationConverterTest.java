package org.chat.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class ConversationConverterTest {
  @InjectMocks private ConversationConverter conversationConverter;

  private Message message;

  private User sender;

  private User target;

  private Group group;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    sender = new User(UUID.randomUUID(), "John.Doe", "Password123+");
    target = new User(UUID.randomUUID(), "Jack.Doe", "Password123+");
    group = new Group(UUID.randomUUID(), "Group");

    message = new Message();
    message.setId(UUID.randomUUID());
    message.setSenderId(sender.getId());
    message.setSenderUsernameSnapshot(sender.getUsername());
    message.setTargetUserId(target.getId());
    message.setTargetUsernameSnapshot(target.getUsername());
    message.setText("some message");
    message.setGroupId(group.getId());
    message.setGroupNameSnapshot(group.getName());
    message.setType(Message.Type.USER);
    message.setTime(Instant.now());
  }

  @Test
  void convertToModelShouldReturnTargetUserDetailsWhenIdIsSenderId() {
    var conversationDto = conversationConverter.convertToModel(message, sender.getId());

    assertNotNull(conversationDto);
    assertEquals(target.getId(), conversationDto.id());
    assertEquals(target.getUsername(), conversationDto.name());
    assertEquals(message.getText(), conversationDto.message());
    assertEquals(message.getType(), conversationDto.messageType());
    assertEquals(message.getTime(), conversationDto.time());
  }

  @Test
  void convertToModelShouldReturnSenderUserDetailsWhenIdIsTargetUserId() {
    var conversationDto = conversationConverter.convertToModel(message, target.getId());

    assertNotNull(conversationDto);
    assertEquals(sender.getId(), conversationDto.id());
    assertEquals(sender.getUsername(), conversationDto.name());
    assertEquals(message.getText(), conversationDto.message());
    assertEquals(message.getType(), conversationDto.messageType());
    assertEquals(message.getTime(), conversationDto.time());
  }

  @Test
  void convertToModelShouldReturnGroupDetailsWhenMessageTypeIsGroup() {
    message.setType(Message.Type.GROUP);

    var conversationDto = conversationConverter.convertToModel(message, target.getId());

    assertNotNull(conversationDto);
    assertEquals(group.getId(), conversationDto.id());
    assertEquals(group.getName(), conversationDto.name());
    assertEquals(sender.getUsername(), conversationDto.senderUsername());
    assertEquals(message.getText(), conversationDto.message());
    assertEquals(message.getType(), conversationDto.messageType());
    assertEquals(message.getTime(), conversationDto.time());
  }
}
