package org.chat.controller;

import jakarta.ws.rs.core.Response;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;
import org.chat.model.UserDto;
import org.chat.service.UserService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ToModelConverter<ContactDto, Contact> contactToModelConverter;

    @Mock
    private ToModelConverter<UserDto, User> userToModelConverter;

    @Mock
    private JsonWebToken jsonWebToken;

    private User user;

    private User target;

    private UserDto userDto;

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

        userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());

        contact = new Contact(UUID.randomUUID(), user, target);
        contactDto = new ContactDto(contact.getId(), target.getId(), target.getUsername());

        when(jsonWebToken.getClaim(USER_ID_CLAIM)).thenReturn(user.getId().toString());
    }

    @Test
    void addContact() {
        when(contactToModelConverter.convertToModel(any(Contact.class)))
                .thenReturn(contactDto);
        when(userService.addContact(any(UUID.class), any(UUID.class))).thenReturn(contact);

        var response = userController.addContact(target.getId());

        assertNotNull(response);
        assertEquals(contactDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(contactToModelConverter).convertToModel(any(Contact.class));
        verify(userService).addContact(any(UUID.class), any(UUID.class));
    }

    @Test
    void getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);

        when(contactToModelConverter.convertToModel(any(Contact.class)))
                .thenReturn(contactDto);
        when(userService.getContacts(any(UUID.class))).thenReturn(contacts);

        var response = userController.getContacts();

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(contactToModelConverter).convertToModel(any(Contact.class));
        verify(userService).getContacts(any(UUID.class));
    }

    @Test
    void searchUserByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userToModelConverter.convertToModel(any(User.class))).thenReturn(userDto);
        when(userService.searchUserByUsername(anyString())).thenReturn(users);

        var response = userController.searchUserByUsername(user.getUsername());

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userToModelConverter).convertToModel(any(User.class));
        verify(userService).searchUserByUsername(anyString());
    }
}