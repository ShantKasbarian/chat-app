package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.ContactService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class ContactServiceImpl implements ContactService {
    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String CONTACT_ALREADY_EXISTS_MESSAGE = "contact already exists";

    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    @Override
    public PanacheQuery<Contact> findByUserId(UUID userId, int page, int size) {
        log.info("fetching contacts of user with id {}, page {}, size {}", userId, page, size);

        var contacts = contactRepository.findByUserId(userId, page, size);

        log.info("fetched contacts of user with id {}, page {}, size {}", userId, page, size);

        return contacts;
    }

    @Override
    @Transactional
    public Contact addContact(UUID userId, UUID targetUserId) {
        log.info("adding user with id {} as contact to user with id {}", targetUserId, userId);

        if (contactRepository.existsByUserIdTargetUserId(userId, targetUserId)) {
            throw new ResourceAlreadyExistsException(CONTACT_ALREADY_EXISTS_MESSAGE);
        }

        User user = userRepository.findByIdOptional(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        Contact contact = new Contact(UUID.randomUUID(), userId, targetUserId, user.getUsername());

        contactRepository.persist(contact);

        log.info("added user with id {} as contact to user with id {}", targetUserId, userId);

        return contact;
    }
}
