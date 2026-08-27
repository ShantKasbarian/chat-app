package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Message;
import org.chat.model.MessageDto;

@ApplicationScoped
public class MessageConverter implements ToModelConverter<MessageDto, Message> {
  @Override
  public MessageDto convertToModel(Message entity) {
    return new MessageDto(
        entity.getId(),
        entity.getSenderId(),
        entity.getSenderUsernameSnapshot(),
        entity.getTargetUserId(),
        entity.getTargetUsernameSnapshot(),
        entity.getText(),
        entity.getTime());
  }
}
