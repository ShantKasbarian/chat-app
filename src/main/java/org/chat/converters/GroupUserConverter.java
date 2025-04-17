package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.GroupUser;
import org.chat.models.GroupUserDto;

@ApplicationScoped
public class GroupUserConverter implements ToModelConverter<GroupUserDto, GroupUser> {
    @Override
    public GroupUserDto convertToModel(GroupUser entity) {
        return new GroupUserDto(
                entity.getId(),
                entity.getGroup().getId(),
                entity.getGroup().getName(),
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getIsMember(),
                entity.getIsCreator()
        );
    }
}
