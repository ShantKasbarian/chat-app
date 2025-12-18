package org.chat.service;

import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ContactServiceImplTest {
    @InjectMocks
    private ContactServiceImpl contactService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

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

        contact = new Contact(UUID.randomUUID(), user1, user2);
    }

    @Test
    void getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);

        when(contactRepository.getContacts(any(UUID.class))).thenReturn(contacts);
        List<Contact> response = contactService.getContacts(user1.getId());

        assertEquals(contacts.size(), response.size());
    }

    @Test
    void addContact() {
        when(userRepository.findById(user1.getId())).thenReturn(user1);
        when(userRepository.findById(user2.getId())).thenReturn(user2);
        doNothing().when(contactRepository).persist(contact);

        Contact response = contactService.addContact(user1.getId(), user2.getId());

        assertEquals(user1.getId(), response.getUser().getId());
        assertEquals(user2.getId(), response.getTarget().getId());
        verify(contactRepository).persist(any(Contact.class));
    }
}
