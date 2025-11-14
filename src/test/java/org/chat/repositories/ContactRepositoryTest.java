package org.chat.repositories;

import com.google.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import org.chat.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ContactRepositoryTest {
    @Inject
    private ContactRepository contactRepository;

    @Inject
    private UserRepository userRepository;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    void getContacts() {
    }
}