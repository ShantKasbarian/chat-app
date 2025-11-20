package org.chat.service;

import org.chat.config.JwtService;
import org.chat.entity.User;
import org.chat.exception.InvalidCredentialsException;
import org.chat.model.TokenDto;
import org.chat.repository.UserRepository;
import org.chat.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {
    private static final String TEST_TOKEN = "test token";

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("John.Doe");
        user.setPassword(BCrypt.hashpw("Password123+", BCrypt.gensalt()));
    }

    @Test
    void login() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));

        when(jwtService.generateToken(anyString(), anyString()))
                .thenReturn(TEST_TOKEN);

        TokenDto response = authenticationService.login(user.getUsername(), "Password123+");

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWithWrongPassword() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(user.getUsername(), "somePassword"));
    }

    @Test
    void createUser() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        doNothing().when(userRepository).persist(any(User.class));

        when(jwtService.generateToken(anyString(), anyString())).thenReturn(TEST_TOKEN);

        TokenDto response = authenticationService.createUser(user.getUsername(), user.getPassword());

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
        verify(userRepository).persist(any(User.class));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUserWithSameUsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUsernameIsLessThan5Characters() {
        user.setUsername("u");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUsernameIsGreaterThan20Characters() {
        String username = "UserUserUserUserUserUser";
        user.setUsername(username);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenPasswordIsInvalid() {
        String invalidPassword = "Password";
        user.setPassword(invalidPassword);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenPasswordIsNull() {
        user.setPassword(null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.createUser(user.getUsername(), null));
    }
}
