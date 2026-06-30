package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ContactServiceImplTest {
    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String CONTACT_ALREADY_EXISTS_MESSAGE = "contact already exists";

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

        contact = new Contact(UUID.randomUUID(), user1.getId(), user2.getId(), user2.getUsername());
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
        when(userRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.of(user2));
        when(contactRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        doNothing().when(contactRepository).persist(contact);

        Contact response = contactService.addContact(user1.getId(), user2.getId());

        assertNotNull(response);
        verify(userRepository).findByIdOptional(any(UUID.class));
        verify(contactRepository).existsByUserIdTargetUserId(any(UUID.class), any(UUID.class));
        verify(contactRepository).persist(any(Contact.class));
    }

    @Test
    void addContactShouldThrowResourceAlreadyExistsExceptionWhenContactAlreadyExists() {
        when(contactRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> contactService.addContact(user1.getId(), user2.getId()));
        assertEquals(CONTACT_ALREADY_EXISTS_MESSAGE, exception.getMessage());

        verify(contactRepository).existsByUserIdTargetUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void addContactShouldThrowResourceNotFoundExceptionWhenTargetUserNotFound() {
        when(contactRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(false);
        when(userRepository.findByIdOptional(any(UUID.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> contactService.addContact(user1.getId(), user2.getId()));
        assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(contactRepository).existsByUserIdTargetUserId(any(UUID.class), any(UUID.class));
        verify(userRepository).findByIdOptional(any(UUID.class));
    }
}
