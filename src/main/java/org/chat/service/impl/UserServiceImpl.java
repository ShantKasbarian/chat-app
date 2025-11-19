package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.ResourceNotFoundException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class UserServiceImpl implements UserService {
    private static final String TARGET_USER_NOT_FOUND_MESSAGE = "target user not found";

    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    public List<Contact> getContacts(String userId) {
        log.info("fetching contacts of user with id {}", userId);

        var contacts = contactRepository.getContacts(userId);

        log.info("fetched contacts of user with id {}", userId);

        return contacts;
    }

    @Override
    @Transactional
    public Contact addContact(String userId, String targetUserId) {
        log.info("adding user with id {} as contact to user with id {}", targetUserId, userId);

        User current = userRepository.findById(userId).get();

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(TARGET_USER_NOT_FOUND_MESSAGE));

        Contact contact = new Contact(UUID.randomUUID().toString(), current, target);

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
