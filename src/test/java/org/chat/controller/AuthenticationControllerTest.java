package org.chat.controller;

import jakarta.ws.rs.core.Response;
import org.chat.entity.User;
import org.chat.model.TokenDto;
import org.chat.model.UserDto;
import org.chat.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {
    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    private User user;

    private UserDto userDto;

    private TokenDto tokenDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("John.Doe");
        user.setPassword(BCrypt.hashpw("Password123+", BCrypt.gensalt()));

        userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());

        tokenDto = new TokenDto("test token");
    }

    @Test
    void login() {
        when(authenticationService.login(anyString(), anyString())).thenReturn(tokenDto);

        var response = authenticationController.login(userDto);

        assertNotNull(response);
        assertEquals(tokenDto, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(authenticationService).login(anyString(), anyString());
    }

    @Test
    void signup() {
        when(authenticationService.createUser(anyString(), anyString())).thenReturn(tokenDto);

        var response = authenticationController.signup(userDto);

        assertNotNull(response);
        assertEquals(tokenDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(authenticationService).createUser(anyString(), anyString());
    }
}
