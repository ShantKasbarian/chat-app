package org.chat.converter;

import org.chat.entity.Group;
import org.chat.model.GroupDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupConverterTest {
    @InjectMocks
    private GroupConverter groupConverter;

    private Group group;

    private GroupDto groupDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        groupDto = new GroupDto(group.getId(), group.getName(), new UUID[]{});
    }

    @Test
    void convertToEntity() {
        Group group = groupConverter.convertToEntity(groupDto);

        assertNotNull(group);
        assertEquals(groupDto.getName(), group.getName());
    }

    @Test
    void convertToModel() {
        GroupDto groupDto = groupConverter.convertToModel(group);

        assertNotNull(groupDto);
        assertEquals(group.getId(), groupDto.getId());
        assertEquals(group.getName(), groupDto.getName());
    }
}