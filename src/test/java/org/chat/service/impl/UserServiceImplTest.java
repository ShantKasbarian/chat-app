package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.User;
import org.chat.exception.InvalidCredentialsException;
import org.chat.model.TokenDto;
import org.chat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private static final String TEST_TOKEN = "test token";

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private PanacheQuery<User> panacheQuery;

    private User user;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
    }

    @Test
    void login() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));

        when(jwtService.generateToken(anyString(), anyString()))
                .thenReturn(TEST_TOKEN);

        TokenDto response = userService.login(user.getUsername(), "Password123+");

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWithWrongPassword() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(InvalidCredentialsException.class, () -> userService.login(user.getUsername(), "somePassword"));
    }

    @Test
    void signUp() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        doNothing().when(userRepository).persist(any(User.class));

        when(jwtService.generateToken(anyString(), anyString())).thenReturn(TEST_TOKEN);

        TokenDto response = userService.signUp(user.getUsername(), user.getPassword());

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
        verify(userRepository).persist(any(User.class));
    }

    @Test
    void signUpWithSameUsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertThrows(InvalidCredentialsException.class, () -> userService.signUp(user.getUsername(), user.getPassword()));
    }

    @Test
    void signUpShouldThrowInvalidCredentialsExceptionWhenUsernameIsLessThan5Characters() {
        user.setUsername("u");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> userService.signUp(user.getUsername(), user.getPassword()));
    }

    @Test
    void signUpShouldThrowInvalidCredentialsExceptionWhenUsernameIsGreaterThan20Characters() {
        String username = "UserUserUserUserUserUser";
        user.setUsername(username);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.signUp(user.getUsername(), user.getPassword()));
    }

    @Test
    void signUpShouldThrowInvalidCredentialsExceptionWhenPasswordIsInvalid() {
        String invalidPassword = "Password";
        user.setPassword(invalidPassword);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.signUp(user.getUsername(), user.getPassword()));
    }

    @Test
    void signUpShouldThrowInvalidCredentialsExceptionWhenPasswordIsNull() {
        user.setPassword(null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.signUp(user.getUsername(), null));
    }

    @Test
    void searchUserByUsername() {
        when(userRepository.findByUsername(anyString(), anyInt(), anyInt()))
                .thenReturn(panacheQuery);

        var response = userService.findByUsername("u", 0, 10);

        assertNotNull(response);
    }
}
