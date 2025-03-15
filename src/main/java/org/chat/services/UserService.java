package org.chat.services;

import io.quarkus.runtime.Startup;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.repositories.ContactRepository;
import org.chat.repositories.UserRepository;

import java.util.List;

@Startup
public class UserService {
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public UserService(
            UserRepository userRepository,
            ContactRepository contactRepository
    ) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    public List<String> getContacts(int userId) {
        return contactRepository.getContacts(userId)
                .stream()
                .map(contact -> contact.getContact().getUsername())
                .toList();
    }

    public String addContact(int userId,String recipientUsername) {
        if (recipientUsername == null) {
            throw new InvalidInfoException("Invalid recipientUsername");
        }

        User contact = userRepository.findByUsername(recipientUsername);
        contactRepository.addContact(userId, contact.getId());

        return "contact has been added";
    }

    public List<String> searchUserByUsername(String username) {
        return userRepository.searchByUsername(username)
                .stream()
                .map(User::getUsername)
                .toList();
    }
}
