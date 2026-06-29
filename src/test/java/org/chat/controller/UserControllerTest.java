package org.chat.controller;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.ws.rs.core.Response;
import org.chat.converter.ToModelConverter;
import org.chat.converter.UserConverter;
import org.chat.entity.User;
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

import static org.chat.service.impl.JwtServiceImpl.USER_ID_CLAIM;
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
    private JsonWebToken jsonWebToken;

    @Mock
    private PanacheQuery<User> panacheQuery;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        User target = new User();
        target.setId(UUID.randomUUID());
        target.setUsername("target");
        target.setPassword("Password123+");

        userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());

        when(jsonWebToken.getClaim(USER_ID_CLAIM)).thenReturn(user.getId().toString());
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