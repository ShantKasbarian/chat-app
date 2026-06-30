package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.User;
import org.chat.exception.InvalidCredentialsException;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.model.TokenDto;
import org.chat.repository.UserRepository;
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

class UserServiceImplTest {
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";

    private static final String USER_WITH_GIVEN_USERNAME_EXISTS_MESSAGE = "a user with the specified username already exists";

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

    private String password;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        password = "Password123+";
        user = new User(UUID.randomUUID(), "user", BCrypt.hashpw(password, BCrypt.gensalt()));
    }

    @Test
    void login() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(anyString(), anyString()))
                .thenReturn(TEST_TOKEN);

        TokenDto response = userService.login(user.getUsername(), password);

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));

        Exception exception = assertThrows(InvalidCredentialsException.class, () -> userService.login(user.getUsername(), "somePassword"));
        assertEquals(INVALID_CREDENTIALS_MESSAGE, exception.getMessage());
    }

    @Test
    void signup() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        doNothing().when(userRepository).persist(any(User.class));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn(TEST_TOKEN);

        TokenDto response = userService.signup(user.getUsername(), user.getPassword());

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).persist(any(User.class));
    }

    @Test
    void signupShouldThrowResourceAlreadyExistsExceptionWhenUserWithUsernameAlreadyExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        Exception exception = assertThrows(ResourceAlreadyExistsException.class, () -> userService.signup(user.getUsername(), user.getPassword()));
        assertEquals(USER_WITH_GIVEN_USERNAME_EXISTS_MESSAGE, exception.getMessage());
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUsername(anyString(), anyInt(), anyInt()))
                .thenReturn(panacheQuery);

        var response = userService.findByUsername("u", 0, 10);

        assertNotNull(response);
    }
}
