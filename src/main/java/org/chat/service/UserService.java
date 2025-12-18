package org.chat.service;

import org.chat.entity.User;

import java.util.List;

public interface UserService {
    List<User> searchUserByUsername(String username);
}
