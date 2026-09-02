package org.chat.mapper;

import org.chat.entity.Group;
import org.chat.model.GroupDto;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {
  GroupDto toModel(Group entity);
}
