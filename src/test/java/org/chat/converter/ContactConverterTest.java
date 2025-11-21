package org.chat.converter;

import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;
import org.chat.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContactConverterTest {
    @InjectMocks
    private ContactConverter contactConverter;

    private Contact contact;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        User target = new User();
        target.setId(UUID.randomUUID());
        target.setUsername("target");
        target.setPassword("Password123+");

        contact = new Contact(UUID.randomUUID(), user, target);
    }

    @Test
    void convertToModel() {
        ContactDto contactDto = contactConverter.convertToModel(contact);

        assertNotNull(contactDto);
        assertEquals(contact.getId(), contactDto.id());
        assertEquals(contact.getTarget().getId(), contactDto.userId());
        assertEquals(contact.getTarget().getUsername(), contactDto.username());
    }
}