package org.chat.repository.impl;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ContactRepositoryImplTest {
    @Inject
    private ContactRepositoryImpl contactRepository;

    @Inject
    private UserRepository userRepository;

    private User user;

    private User target;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("user");
        user.setPassword("Password123+");

        target = new User();
        target.setUsername("target");
        target.setPassword("Password123+");
    }

    @Test
    @TestTransaction
    void getContacts() {
        userRepository.persist(user);
        userRepository.persist(target);

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setTarget(target);

        contactRepository.persist(contact);

        List<Contact> contacts = contactRepository.getContacts(user.getId());

        assertNotNull(contacts);
        assertFalse(contacts.isEmpty());
    }
}