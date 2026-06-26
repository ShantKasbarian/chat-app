package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupUserDto;

@ApplicationScoped
public class GroupUserConverter implements ToModelConverter<GroupUserDto, GroupUser> {
    @Override
    public GroupUserDto convertToModel(GroupUser entity) {
        return new GroupUserDto(
                entity.getId(),
                entity.getGroupId(),
                entity.getUserId(),
                entity.getUsernameSnapshot(),
                entity.getRole()
        );
    }
}
