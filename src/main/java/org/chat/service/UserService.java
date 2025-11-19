package org.chat.service;

import org.chat.entity.Contact;
import org.chat.entity.User;

import java.util.List;

public interface UserService {
    Contact addContact(String userId, String targetUserId);
    List<User> searchUserByUsername(String username);
}
