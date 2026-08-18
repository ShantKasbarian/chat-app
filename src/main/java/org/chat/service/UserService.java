package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.User;
import org.chat.model.TokenDto;

public interface UserService {
  TokenDto login(String username, String password);

  TokenDto signup(String username, String password);

  PanacheQuery<User> findByUsername(String username, int page, int size);
}
