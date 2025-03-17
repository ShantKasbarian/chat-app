package org.chat.services;

import io.quarkus.runtime.Startup;
import jakarta.transaction.Transactional;
import org.chat.entities.Contact;
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

    public List<String> getContacts(Long userId) {
        return contactRepository.getContacts(userId)
                .stream()
                .map(contact -> contact.getContact().getUsername())
                .toList();
    }

    @Transactional
    public Contact addContact(Long userId, String recipientUsername) {
        if (recipientUsername == null) {
            throw new InvalidInfoException("Invalid recipientUsername");
        }

        User current = userRepository.findById(userId);
        User target = userRepository.findByUsername(recipientUsername);
        Contact contact = new Contact(current, target);

        contact.persist();
        return contact;
    }

    public List<String> searchUserByUsername(String username) {
        return userRepository.searchByUsername(username)
                .stream()
                .map(User::getUsername)
                .toList();
    }
}
