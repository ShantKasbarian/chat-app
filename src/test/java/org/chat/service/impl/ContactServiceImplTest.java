package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ContactServiceImplTest {
    @InjectMocks
    private ContactServiceImpl contactService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PanacheQuery<Contact> panacheQuery;

    private User user1;

    private User user2;

    private Contact contact;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");

        contact = new Contact(UUID.randomUUID(), user1.getId(), user2.getId());
    }

    @Test
    void findByUserId() {
        when(contactRepository.findByUserId(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        var response = contactService.findByUserId(user1.getId(), 0, 10);

        assertNotNull(response);
    }

    @Test
    void addContact() {
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(userRepository.findById(user2.getId())).thenReturn(user2);
        when(contactRepository.existsByUserIdTargetUserId(user1.getId(), user2.getId()))
                .thenReturn(false);
        doNothing().when(contactRepository).persist(contact);

        Contact response = contactService.addContact(user1.getId(), user2.getId());

        assertEquals(user1.getId(), response.getUserId());
        assertEquals(user2.getId(), response.getTargetUserId());
        verify(contactRepository).persist(any(Contact.class));
    }

    @Test
    void addContactShouldThrowResourceAlreadyExistsExceptionWhenContactAlreadyExists() {
        when(userRepository.findById(any(UUID.class))).thenReturn(user1);
        when(contactRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> contactService.addContact(user1.getId(), user2.getId()));
    }
}
