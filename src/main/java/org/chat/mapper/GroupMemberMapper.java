package org.chat.mapper;

import org.chat.entity.GroupMember;
import org.chat.model.GroupMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GroupMemberMapper {
  @Mapping(target = "username", source = "usernameSnapshot")
  GroupMemberDto toModel(GroupMember entity);
}
