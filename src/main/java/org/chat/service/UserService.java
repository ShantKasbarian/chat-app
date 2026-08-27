package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.User;
import org.chat.model.LoginDto;
import org.chat.model.TokenDto;
import org.chat.model.UserDto;

public interface UserService {
  TokenDto login(LoginDto loginDto);

  TokenDto signup(UserDto userDto);

  PanacheQuery<User> findByUsername(String username, int page, int size);
}
