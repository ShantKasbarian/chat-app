package org.chat.controllerTests.IT;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.chat.controllers.LoginSignupController;
import org.chat.entities.User;
import org.chat.models.UserDto;
import org.chat.services.LoginSignupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

@QuarkusTest
class LoginSignupControllerIT {
    @Inject
    private LoginSignupController loginSignupController;

    @InjectMock
    private LoginSignupService loginSignupService;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("username");
        user.setPassword("Password123+");

        userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
    }

    @Test
    void login() {
        Mockito.when(loginSignupService.login(user.getUsername(), user.getPassword()))
                .thenReturn("some token");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200);
    }

    @Test
    void signup() {
        Mockito.when(loginSignupService.createUser(user.getUsername(), user.getPassword()))
                .thenReturn("user successfully registered");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .when()
                .post("/auth/signup")
                .then()
                .statusCode(201);
    }
}