package org.chat.mapper;

import org.chat.entity.User;
import org.chat.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
  @Mapping(target = "password", ignore = true)
  UserDto toModel(User entity);
}
