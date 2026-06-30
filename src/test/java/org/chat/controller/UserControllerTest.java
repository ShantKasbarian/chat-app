package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.UserConverter;
import org.chat.entity.User;
import org.chat.model.LoginDto;
import org.chat.model.TokenDto;
import org.chat.model.UserDto;
import org.chat.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserConverter userConverter;

    @Mock
    private PanacheQuery<User> panacheQuery;

    private User user;

    private LoginDto loginDto;

    private UserDto userDto;

    private TokenDto tokenDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(UUID.randomUUID(), "John.Doe", BCrypt.hashpw("Password123+", BCrypt.gensalt()));

        User target = new User();
        target.setId(UUID.randomUUID());
        target.setUsername("target");
        target.setPassword("Password123+");

        loginDto = new LoginDto(user.getUsername(), user.getPassword());
        userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
        tokenDto = new TokenDto("test token");
    }

    @Test
    void login() {
        when(userService.login(anyString(), anyString())).thenReturn(tokenDto);

        var response = userController.login(loginDto);

        assertNotNull(response);
        assertEquals(tokenDto, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userService).login(anyString(), anyString());
    }

    @Test
    void signup() {
        when(userService.signup(anyString(), anyString())).thenReturn(tokenDto);

        var response = userController.signup(userDto);

        assertNotNull(response);
        assertEquals(tokenDto, response.getEntity());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(userService).signup(anyString(), anyString());
    }

    @Test
    void findByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userService.findByUsername(anyString(), anyInt(), anyInt()))
                .thenReturn(panacheQuery);
        when(userConverter.convertToModel(any(User.class))).thenReturn(userDto);
        when(panacheQuery.list()).thenReturn(users);
        when(panacheQuery.count()).thenReturn(10L);
        when(panacheQuery.pageCount()).thenReturn(1);

        var response = userController.findByUsername(user.getUsername(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userConverter, times(users.size())).convertToModel(any(User.class));
        verify(userService).findByUsername(anyString(), anyInt(), anyInt());
    }
}