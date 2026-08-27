package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.User;
import org.chat.model.UserDto;

@ApplicationScoped
public class UserConverter implements ToModelConverter<UserDto, User> {

  @Override
  public UserDto convertToModel(User entity) {
    return new UserDto(entity.getId(), entity.getUsername(), null);
  }
}
