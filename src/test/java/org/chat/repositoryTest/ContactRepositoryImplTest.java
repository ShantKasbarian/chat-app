package org.chat.repositoryTest;

import com.google.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import org.chat.entity.User;
import org.chat.repository.impl.ContactRepositoryImpl;
import org.chat.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ContactRepositoryImplTest {
    @Inject
    private ContactRepositoryImpl contactRepository;

    @Inject
    private UserRepositoryImpl userRepository;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    void getContacts() {
    }
}