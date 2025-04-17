package org.chat.controllerTests.IT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.config.JwtService;
import org.chat.controllers.UserController;
import org.chat.converters.ContactConverter;
import org.chat.converters.UserConverter;
import org.chat.entities.Contact;
import org.chat.entities.User;
import org.chat.models.ContactDto;
import org.chat.models.UserDto;
import org.chat.services.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@QuarkusTest
class UserControllerIT {
    @Inject
    private UserController userController;

    @InjectMock
    private UserService userService;

    @InjectMock
    private SecurityContext securityContext;

    @InjectMock
    private JsonWebToken token;

    @InjectMock
    private UserConverter userConverter;

    @InjectMock
    private ContactConverter contactConverter;

    @Inject
    private JwtService jwtService;

    private User user1;

    private User user2;

    private UserDto userDto1;

    private UserDto userDto2;

    private ContactDto contactDto;

    private Contact contact;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setUsername("username1");
        user1.setPassword("password");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setUsername("username2");
        user2.setPassword("password");

        userDto1 = new UserDto(user1.getId(), user1.getUsername(), user1.getPassword());
        userDto2 = new UserDto(user2.getId(), user2.getUsername(), user2.getPassword());

        contact = new Contact(UUID.randomUUID().toString(), user1, user2);

        contactDto = new ContactDto(
                contact.getId(),
                contact.getUser().getId(),
                contact.getUser().getUsername()
        );
    }

    @Test
    void addContact() {
        when(userService.addContact(user1.getId(), user2.getId()))
                .thenReturn(contact);
        when(contactConverter.convertToModel(contact)).thenReturn(contactDto);

        String jwtToken = jwtService.generateToken(user1.getUsername(), user1.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .post("/user/" + user2.getId() + "/add/contact")
                .then()
                .statusCode(201);
    }

    @Test
    void getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);

        when(contactConverter.convertToModel(contact)).thenReturn(contactDto);
        when(userService.getContacts(user1.getId())).thenReturn(contacts);

        String jwtToken = jwtService.generateToken(user1.getUsername(), user1.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/user/contacts")
                .then()
                .statusCode(200);
    }

    @Test
    void searchUserByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userConverter.convertToModel(user1)).thenReturn(userDto1);
        when(userConverter.convertToModel(user2)).thenReturn(userDto2);
        when(userService.searchUserByUsername("u")).thenReturn(users);

        String jwtToken = jwtService.generateToken(user1.getUsername(), user1.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/user/u/search")
                .then()
                .statusCode(200);
    }
}