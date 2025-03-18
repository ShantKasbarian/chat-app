package org.chat.controllerTests;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.config.JwtService;
import org.chat.controllers.UserController;
import org.chat.entities.Contact;
import org.chat.entities.User;
import org.chat.services.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@QuarkusTest
class UserControllerTest {
    @Inject
    private UserController userController;

    @InjectMock
    private UserService userService;

    @InjectMock
    private SecurityContext securityContext;

    @InjectMock
    private JsonWebToken token;

    @Inject
    private JwtService jwtService;

    private User user1;

    private User user2;

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
    }

    @Test
    void addContact() {
        Contact contact = new Contact(UUID.randomUUID().toString(), user1, user2);
        
        when(userService.addContact(user1.getId(), user2.getUsername()))
                .thenReturn(contact);

        String jwtToken = jwtService.generateToken(user1.getUsername(), user1.getId());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .post("/user/" + user2.getUsername() + "/add/contact")
                .then()
                .statusCode(201);
    }

    @Test
    void getContacts() {
        List<String> contacts = new ArrayList<>();
        contacts.add("contact1");
        contacts.add("contact2");
        contacts.add("contact3");

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
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");
        users.add("user3");

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