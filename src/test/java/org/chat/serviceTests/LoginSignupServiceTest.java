package org.chat.serviceTests;

import org.chat.config.JwtService;
import org.chat.entities.User;
import org.chat.exceptions.InvalidCredentialsException;
import org.chat.repositories.UserRepository;
import org.chat.services.LoginSignupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginSignupServiceTest {
    @InjectMocks
    private LoginSignupService loginSignupService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("userUser");
        user.setPassword(BCrypt.hashpw("Password123+", BCrypt.gensalt()));
    }

    @Test
    void login() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(user);

        String expected = "some token";

        when(jwtService.generateToken(user.getUsername(), user.getId()))
                .thenReturn(expected);

        String response = loginSignupService.login(user.getUsername(), "Password123+");

        assertEquals(expected, response);
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWithWrongPassword() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.login(user.getUsername(), "somePassword"));
    }

    @Test
    void createUser() {
        when(userRepository.findByUsername("user")).thenReturn(null);
        doNothing().when(userRepository).persist(user);

        String expected = "user successfully registered";
        String response = loginSignupService.createUser(user.getUsername(), user.getPassword());

        assertEquals(expected, response);
        verify(userRepository, times(1)).persist(any(User.class));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUserWithSameUsernameExists() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUsernameIsLessThan5Characters() {
        user.setUsername("u");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUsernameIsGreaterThan20Characters() {
        user.setUsername("UserUserUserUserUserUser");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenPasswordIsInvalid() {
        user.setPassword("Password");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenPasswordIsNull() {
        user.setPassword("Password");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), null));
    }
}