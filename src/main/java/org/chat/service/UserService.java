package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.User;
import org.chat.model.TokenDto;

import java.util.UUID;

public interface UserService {
    TokenDto login(String username, String password);
    TokenDto signUp(String username, String password);
    User findById(UUID id);
    PanacheQuery<User> findByUsername(String username, int page, int size);
}
