package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.InvalidInfoException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    public List<Contact> getContacts(String userId) {
        return contactRepository.getContacts(userId);
    }

    @Override
    @Transactional
    public Contact addContact(String userId, String recipientId) {
        if (recipientId == null || recipientId.isEmpty()) {
            throw new InvalidInfoException("Invalid recipientId");
        }

        User current = userRepository.findById(userId);
        User target = userRepository.findById(recipientId);
        Contact contact = new Contact(UUID.randomUUID().toString(), current, target);

        contactRepository.persist(contact);
        return contact;
    }

    @Override
    public List<User> searchUserByUsername(String username) {
        return userRepository.searchByUsername(username);
    }
}
