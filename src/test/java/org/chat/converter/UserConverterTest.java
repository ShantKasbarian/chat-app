package org.chat.converter;

import org.chat.entity.User;
import org.chat.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserConverterTest {
    @InjectMocks
    private UserConverter userConverter;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        userDto = new UserDto(user.getId(), user.getUsername(), user.getPassword());
    }

    @Test
    void convertToEntity() {
        User user = userConverter.convertToEntity(userDto);

        assertNotNull(user);
        assertEquals(userDto.username(), user.getUsername());
        assertEquals(userDto.password(), user.getPassword());
    }

    @Test
    void convertToModel() {
        UserDto userDto = userConverter.convertToModel(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getUsername(), userDto.username());
        assertNull(userDto.password());
    }
}