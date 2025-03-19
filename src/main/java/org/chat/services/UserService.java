package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.chat.entities.Contact;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.repositories.ContactRepository;
import org.chat.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
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

    public List<String> getContacts(String userId) {
        return contactRepository.getContacts(userId)
                .stream()
                .map(contact -> contact.getContact().getUsername())
                .toList();
    }

    @Transactional
    public Contact addContact(String userId, String recipientUsername) {
        if (recipientUsername == null || recipientUsername.isEmpty()) {
            throw new InvalidInfoException("Invalid recipientUsername");
        }

        User current = userRepository.findById(userId);
        User target = userRepository.findByUsername(recipientUsername);
        Contact contact = new Contact(UUID.randomUUID().toString(), current, target);

        contactRepository.persist(contact);
        return contact;
    }

    public List<String> searchUserByUsername(String username) {
        return userRepository.searchByUsername(username)
                .stream()
                .map(User::getUsername)
                .toList();
    }
}
