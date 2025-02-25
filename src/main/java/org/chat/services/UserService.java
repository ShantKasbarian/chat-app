package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
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
}
