package org.chat.mapper;

import java.util.UUID;
import org.chat.entity.Message;
import org.chat.model.ConversationDto;
import org.mapstruct.Mapper;

@Mapper
public interface ConversationMapper {
  default ConversationDto toModel(Message entity, UUID currentUserId) {
    Message.Type type = entity.getType();
    UUID id = null;
    String name = null;
    String sender = null;

    if (type == Message.Type.GROUP) {
      id = entity.getGroupId();
      name = entity.getGroupNameSnapshot();
      sender = entity.getSenderUsernameSnapshot();
    } else if (type == Message.Type.USER && entity.getSenderId().equals(currentUserId)) {
      id = entity.getTargetUserId();
      name = entity.getTargetUsernameSnapshot();
    } else {
      id = entity.getSenderId();
      name = entity.getSenderUsernameSnapshot();
    }

    return new ConversationDto(id, name, sender, entity.getText(), type, entity.getTime());
  }
}
