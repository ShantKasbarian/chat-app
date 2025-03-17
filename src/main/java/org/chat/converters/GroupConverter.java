package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Group;
import org.chat.models.GroupDto;

@ApplicationScoped
public class GroupConverter implements
        ToEntityConverter<Group, GroupDto>,
        ToModelConverter<GroupDto, Group>
{

    @Override
    public Group convertToEntity(GroupDto model) {
        Group group = new Group();
        group.setId(model.getId());
        group.setName(model.getName());

        return group;
    }

    @Override
    public GroupDto convertToModel(Group entity) {
        GroupDto groupDto = new GroupDto();
        groupDto.setId(entity.getId());
        groupDto.setName(entity.getName());

        return groupDto;
    }
}
