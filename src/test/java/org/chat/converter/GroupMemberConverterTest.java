package org.chat.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.User;
import org.chat.model.GroupMemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class GroupMemberConverterTest {
  @InjectMocks private GroupMemberConverter groupMemberConverter;

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

    groupMember =
        new GroupMember(
            UUID.randomUUID(),
            group.getId(),
            user.getId(),
            user.getUsername(),
            GroupMember.Role.ADMIN);
  }

  @Test
  void convertToModel() {
    GroupMemberDto groupMemberDto = groupMemberConverter.convertToModel(groupMember);

    assertNotNull(groupMemberDto);
    assertEquals(groupMember.getId(), groupMemberDto.id());
    assertEquals(groupMember.getGroupId(), groupMemberDto.groupId());
    assertEquals(groupMember.getUserId(), groupMemberDto.userId());
    assertEquals(groupMember.getUsernameSnapshot(), groupMemberDto.username());
    assertEquals(groupMember.getRole(), groupMemberDto.role());
  }
}
