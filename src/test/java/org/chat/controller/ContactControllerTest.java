package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.ContactConverter;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;
import org.chat.security.UserContext;
import org.chat.security.UserPrincipal;
import org.chat.service.ContactService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.chat.service.impl.JwtServiceImpl.USER_ID_CLAIM;
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
    private ContactConverter contactConverter;

    @Mock
    private UserContext userContext;

    @Mock
    private PanacheQuery<Contact> panacheQuery;

    private User target;

    private Contact contact;

    private ContactDto contactDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        target = new User();
        target.setId(UUID.randomUUID());
        target.setUsername("target");
        target.setPassword("Password123+");

        contact = new Contact(UUID.randomUUID(), user.getId(), target.getId(), target.getUsername());
        contactDto = new ContactDto(contact.getId(), target.getId(), target.getUsername());

        UserPrincipal userPrincipal = new UserPrincipal(user.getId(), user.getUsername());
        when(userContext.get()).thenReturn(userPrincipal);
    }

    @Test
    void getContacts() {
        when(contactService.findByUserId(any(UUID.class), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of(contact));
        when(contactConverter.convertToModel(any(Contact.class)))
                .thenReturn(contactDto);
        when(panacheQuery.count()).thenReturn(10L);
        when(panacheQuery.pageCount()).thenReturn(1);

        var response = contactController.getContacts(0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(contactConverter, atLeast(1)).convertToModel(any(Contact.class));
        verify(contactService).findByUserId(any(UUID.class), anyInt(), anyInt());
    }

    @Test
    void addContact() {
        when(contactService.addContact(any(UUID.class), any(UUID.class)))
                .thenReturn(contact);
        when(contactConverter.convertToModel(any(Contact.class)))
                .thenReturn(contactDto);

        var response = contactController.addContact(target.getId());

        assertNotNull(response);
        assertEquals(contactDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(contactService).addContact(any(UUID.class), any(UUID.class));
        verify(contactConverter).convertToModel(any(Contact.class));
    }
}
