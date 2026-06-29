package org.chat.converter;

import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.User;
import org.chat.model.GroupUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupMemberConverterTest {
    @InjectMocks
    private GroupMemberConverter groupMemberConverter;

    private GroupMember groupMember;

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

        groupMember = new GroupMember(UUID.randomUUID(), group.getId(), user.getId(), user.getUsername(), GroupMember.Role.ADMIN);

    }

    @Test
    void convertToModel() {
        GroupUserDto groupUserDto = groupMemberConverter.convertToModel(groupMember);

        assertNotNull(groupUserDto);
        assertEquals(groupMember.getId(), groupUserDto.id());
        assertEquals(groupMember.getGroupId(), groupUserDto.groupId());
        assertEquals(groupMember.getUserId(), groupUserDto.userId());
        assertEquals(groupMember.getUsernameSnapshot(), groupUserDto.username());
        assertEquals(groupMember.getRole(), groupUserDto.role());
    }
}