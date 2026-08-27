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

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    group = new Group();
    group.setId(UUID.randomUUID());
    group.setName("group");
  }

  @Test
  void convertToModel() {
    GroupDto groupDto = groupConverter.convertToModel(group);

    assertNotNull(groupDto);
    assertEquals(group.getId(), groupDto.id());
    assertEquals(group.getName(), groupDto.name());
  }
}
