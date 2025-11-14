package org.chat.serviceTest;

import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.exception.InvalidInfoException;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactRepository contactRepository;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setUsername("user2");
    }

    @Test
    void getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(UUID.randomUUID().toString(), user2, user1));

        when(contactRepository.getContacts(user2.getId())).thenReturn(contacts);
        List<Contact> response = userService.getContacts(user2.getId());

        assertEquals(contacts.size(), response.size());
    }

    @Test
    void addContact() {
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(userRepository.findById(user2.getId())).thenReturn(user2);

        Contact contact = new Contact(UUID.randomUUID().toString(), user1, user2);
        doNothing().when(contactRepository).persist(contact);

        Contact response = userService.addContact(user1.getId(), user2.getId());

        assertEquals(user1.getId(), response.getUser().getId());
        assertEquals(user2.getId(), response.getContact().getId());
        verify(contactRepository, times(1)).persist(any(Contact.class));
    }

    @Test
    void addContactShouldThrowInvalidInfoExceptionWhenRecipientIdIsNull() {
        assertThrows(InvalidInfoException.class, () -> userService.addContact(user1.getId(), null));
    }

    @Test
    void addContactShouldThrowInvalidInfoExceptionWhenRecipientIdIsEmpty() {
        assertThrows(InvalidInfoException.class, () -> userService.addContact(user1.getId(), ""));
    }

    @Test
    void searchUserByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.searchByUsername("u")).thenReturn(users);

        List<User> response = userService.searchUserByUsername("u");

        assertNotNull(response);
        assertEquals(users.size(), response.size());
    }
}