package org.chat.controller;

import jakarta.ws.rs.core.Response;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;
import org.chat.service.ContactService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.chat.config.JwtService.USER_ID_CLAIM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ContactControllerTest {
    @InjectMocks
    private ContactController contactController;

    @Mock
    private ContactService contactService;

    @Mock
    private ToModelConverter<ContactDto, Contact> contactToModelConverter;

    @Mock
    private JsonWebToken jsonWebToken;

    private User user;

    private User target;

    private Contact contact;

    private ContactDto contactDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        target = new User();
        target.setId(UUID.randomUUID());
        target.setUsername("target");
        target.setPassword("Password123+");

        contact = new Contact(UUID.randomUUID(), user, target);
        contactDto = new ContactDto(contact.getId(), target.getId(), target.getUsername());

        when(jsonWebToken.getClaim(USER_ID_CLAIM)).thenReturn(user.getId().toString());
    }

    @Test
    void getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);

        when(contactToModelConverter.convertToModel(any(Contact.class)))
                .thenReturn(contactDto);
        when(contactService.getContacts(any(UUID.class))).thenReturn(contacts);

        var response = contactController.getContacts();

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(contactToModelConverter, times(contacts.size())).convertToModel(any(Contact.class));
        verify(contactService).getContacts(any(UUID.class));
    }

    @Test
    void addContact() {
        when(contactToModelConverter.convertToModel(any(Contact.class)))
                .thenReturn(contactDto);
        when(contactService.addContact(any(UUID.class), any(UUID.class))).thenReturn(contact);

        var response = contactController.addContact(target.getId());

        assertNotNull(response);
        assertEquals(contactDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(contactToModelConverter).convertToModel(any(Contact.class));
        verify(contactService).addContact(any(UUID.class), any(UUID.class));
    }
}
