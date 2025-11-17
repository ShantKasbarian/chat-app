package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.GroupUser;
import org.chat.model.GroupUserDto;

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
