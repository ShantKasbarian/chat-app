package org.chat.converter;

import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupUserConverterTest {
    @InjectMocks
    private GroupUserConverter groupUserConverter;

    private GroupUser groupUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("group");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPassword("Password123+");

        groupUser = new GroupUser(UUID.randomUUID(), group, user, false, false);

    }

    @Test
    void convertToModel() {
        GroupUserDto groupUserDto = groupUserConverter.convertToModel(groupUser);

        assertNotNull(groupUserDto);
        assertEquals(groupUser.getId(), groupUserDto.id());
        assertEquals(groupUser.getGroup().getId(), groupUserDto.groupId());
        assertEquals(groupUser.getGroup().getName(), groupUserDto.groupName());
        assertEquals(groupUser.getUser().getId(), groupUserDto.userId());
        assertEquals(groupUser.getUser().getUsername(), groupUserDto.username());
        assertEquals(groupUser.getIsMember(), groupUserDto.isMember());
        assertEquals(groupUser.getIsCreator(), groupUserDto.isCreator());
    }
}