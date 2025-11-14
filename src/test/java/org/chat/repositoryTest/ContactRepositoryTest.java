package org.chat.repositoryTest;

import com.google.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import org.chat.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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