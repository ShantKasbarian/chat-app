package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Group;
import org.chat.model.GroupDto;

@ApplicationScoped
public class GroupConverter implements ToModelConverter<GroupDto, Group> {
  @Override
  public GroupDto convertToModel(Group entity) {
    return new GroupDto(entity.getId(), entity.getName());
  }
}
