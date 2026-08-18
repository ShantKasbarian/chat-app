package org.chat.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.chat.entity.Group;
import org.chat.model.GroupDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class GroupConverterTest {
  @InjectMocks private GroupConverter groupConverter;

  private Group group;

  private GroupDto groupDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    group = new Group();
    group.setId(UUID.randomUUID());
    group.setName("group");

    groupDto = new GroupDto(group.getId(), group.getName());
  }

  @Test
  void convertToEntity() {
    Group group = groupConverter.convertToEntity(groupDto);

    assertNotNull(group);
    assertEquals(groupDto.name(), group.getName());
  }

  @Test
  void convertToModel() {
    GroupDto groupDto = groupConverter.convertToModel(group);

    assertNotNull(groupDto);
    assertEquals(group.getId(), groupDto.id());
    assertEquals(group.getName(), groupDto.name());
  }
}
