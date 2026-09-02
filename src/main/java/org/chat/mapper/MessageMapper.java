package org.chat.mapper;

import org.chat.entity.Message;
import org.chat.model.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MessageMapper {
  @Mapping(target = "senderUsername", source = "senderUsernameSnapshot")
  @Mapping(target = "targetUsername", source = "targetUsernameSnapshot")
  MessageDto toModel(Message entity);
}
