package org.chat.serviceTest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.chat.config.JwtService;
import org.chat.entity.User;
import org.chat.exception.InvalidCredentialsException;
import org.chat.model.TokenDto;
import org.chat.repository.impl.UserRepositoryImpl;
import org.chat.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {
    private static final String TEST_TOKEN = "test token";

    @InjectMocks
    private AuthenticationServiceImpl loginSignupService;

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PanacheQuery<User> mockQuery;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("userUser");
        user.setPassword(BCrypt.hashpw("Password123+", BCrypt.gensalt()));

        when(userRepository.find(anyString(), anyString())).thenReturn(mockQuery);
    }

    @Test
    void login() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(user);

        String expected = "some token";

        when(jwtService.generateToken(user.getUsername(), user.getId()))
                .thenReturn(expected);

        TokenDto response = loginSignupService.login(user.getUsername(), "Password123+");

        assertNotNull(response);
        assertEquals(expected, response.token());
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWithWrongPassword() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.login(user.getUsername(), "somePassword"));
    }

    @Test
    void createUser() {
        when(userRepository.find("username", user.getUsername()).firstResult()).thenReturn(null);
        doNothing().when(userRepository).persist(user);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn(TEST_TOKEN);

        String expected = "user successfully registered";
        TokenDto response = loginSignupService.createUser(user.getUsername(), user.getPassword());

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
        verify(userRepository, times(1)).persist(any(User.class));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUserWithSameUsernameExists() {
        when(userRepository.find("username", user.getUsername()).firstResult()).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUsernameIsLessThan5Characters() {
        user.setUsername("u");
        when(userRepository.find("username", user.getUsername()).firstResult()).thenReturn(null);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenUsernameIsGreaterThan20Characters() {
        user.setUsername("UserUserUserUserUserUser");
        when(userRepository.find("username", user.getUsername()).firstResult()).thenReturn(null);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenPasswordIsInvalid() {
        user.setPassword("Password");
        when(userRepository.find("username", user.getUsername()).firstResult()).thenReturn(null);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), user.getPassword()));
    }

    @Test
    void createUserShouldThrowInvalidCredentialsExceptionWhenPasswordIsNull() {
        user.setPassword("Password");
        when(userRepository.find("username", user.getUsername()).firstResult()).thenReturn(user);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.createUser(user.getUsername(), null));
    }
}