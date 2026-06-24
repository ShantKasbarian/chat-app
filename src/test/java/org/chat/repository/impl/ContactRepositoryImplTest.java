package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.chat.config.MongoConfig;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Testcontainers
class ContactRepositoryImplTest {
    @Container
    static MongoDBContainer mongo = MongoConfig.getContainer();

    @Inject
    private ContactRepositoryImpl contactRepository;

    @Inject
    private UserRepository userRepository;

    private User user;

    private User target;

    private Contact contact;

    static {
        mongo.start();
    }

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
        contact.setUserId(user.getId());
        contact.setTargetUserId(target.getId());
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
        PanacheQuery<Contact> contacts = contactRepository.getContacts(user.getId(), 0, 10);

        assertNotNull(contacts);
        assertFalse(contacts.list().isEmpty());
    }

    @Test
    void existsByUserIdTargetUserId() {
        assertTrue(contactRepository.existsByUserIdTargetUserId(user.getId(), target.getId()));
    }
}
