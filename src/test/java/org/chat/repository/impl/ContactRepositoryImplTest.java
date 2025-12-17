package org.chat.repository.impl;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
class ContactRepositoryImplTest {
    @Inject
    private ContactRepositoryImpl contactRepository;

    @Inject
    private UserRepository userRepository;

    private User user;

    private User target;

    private Contact contact;

    @BeforeEach
    @Transactional
    void setUp() {
        user = new User();
        user.setUsername("user");
        user.setPassword("Password123+");

        target = new User();
        target.setUsername("target");
        target.setPassword("Password123+");

        userRepository.persist(user);
        userRepository.persist(target);

        contact = new Contact();
        contact.setUser(user);
        contact.setTarget(target);
        contactRepository.persist(contact);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userRepository.delete(user);
        userRepository.delete(target);
        contactRepository.delete(contact);
    }

    @Test
    void getContacts() {
        List<Contact> contacts = contactRepository.getContacts(user.getId());

        assertNotNull(contacts);
        assertFalse(contacts.isEmpty());
    }
}
