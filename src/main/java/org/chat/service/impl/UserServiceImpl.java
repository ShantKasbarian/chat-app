package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    @Override
    public List<Contact> getContacts(UUID userId) {
        log.info("fetching contacts of user with id {}", userId);

        var contacts = contactRepository.getContacts(userId);

        log.info("fetched contacts of user with id {}", userId);

        return contacts;
    }

    @Override
    @Transactional
    public Contact addContact(UUID userId, UUID targetUserId) {
        log.info("adding user with id {} as contact to user with id {}", targetUserId, userId);

        User current = userRepository.findById(userId);

        User target = userRepository.findById(targetUserId);

        Contact contact = new Contact();
        contact.setUser(current);
        contact.setContact(target);

        contactRepository.persist(contact);

        log.info("added user with id {} as contact to user with id {}", targetUserId, userId);

        return contact;
    }

    @Override
    public List<User> searchUserByUsername(String username) {
        log.info("fetching users with username {}", username);

        var users = userRepository.searchByUsername(username);

        log.info("fetched users with username {}", username);

        return users;
    }
}
