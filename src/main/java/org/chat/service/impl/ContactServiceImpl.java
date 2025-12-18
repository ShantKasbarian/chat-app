package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.ContactService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class ContactServiceImpl implements ContactService {
    private static final String CONTACT_ALREADY_EXISTS_MESSAGE = "contact already exists";

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

        if (contactRepository.existsByUserIdTargetUserId(userId, targetUserId)) {
            throw new ResourceAlreadyExistsException(CONTACT_ALREADY_EXISTS_MESSAGE);
        }

        Contact contact = new Contact();
        contact.setUser(current);
        contact.setTarget(target);

        contactRepository.persist(contact);

        log.info("added user with id {} as contact to user with id {}", targetUserId, userId);

        return contact;
    }
}
