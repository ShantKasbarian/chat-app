package org.chat.repository.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
class ContactRepositoryImplTest {
    @Inject
    private ContactRepositoryImpl contactRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getContacts() {
    }
}