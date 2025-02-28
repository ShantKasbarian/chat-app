package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.User;
import org.chat.repositories.UserRepository;

import java.util.List;

@ApplicationScoped
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<String> getContacts(int userId) {
        return userRepository.getContacts(userId);
    }

    public String addContact(int userId,String recipientUsername) {
        if (recipientUsername == null) {
            throw new RuntimeException("Invalid recipientUsername");
        }

        User contact = userRepository.findByUsername(recipientUsername);

        if (contact == null) {
            throw new RuntimeException("Contact not found");
        }

        userRepository.addContact(userId, contact.getId());

        return "contact has been added";
    }

    public List<String> searchUserByUsername(String username) {
        return userRepository.getUsersByUsername(username);
    }
}
