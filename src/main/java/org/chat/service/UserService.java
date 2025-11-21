package org.chat.service;

import org.chat.entity.Contact;
import org.chat.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<Contact> getContacts(UUID userId);
    Contact addContact(UUID userId, UUID targetUserId);
    List<User> searchUserByUsername(String username);
}
