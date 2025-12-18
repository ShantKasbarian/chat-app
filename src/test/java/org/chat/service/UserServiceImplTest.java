package org.chat.service;

import org.chat.entity.User;
import org.chat.repository.UserRepository;
import org.chat.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
    }

    @Test
    void searchUserByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.searchByUsername(anyString())).thenReturn(users);

        List<User> response = userService.searchUserByUsername("u");

        assertNotNull(response);
        assertEquals(users.size(), response.size());
    }
}
