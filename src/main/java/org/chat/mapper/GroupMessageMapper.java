package org.chat.mapper;

import org.chat.entity.Message;
import org.chat.model.GroupMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GroupMessageMapper {
  @Mapping(target = "senderUsername", source = "senderUsernameSnapshot")
  @Mapping(target = "groupName", source = "groupNameSnapshot")
  GroupMessageDto toModel(Message entity);
}
