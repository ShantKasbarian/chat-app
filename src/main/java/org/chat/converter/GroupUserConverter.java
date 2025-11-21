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
        Group group = entity.getGroup();
        User user = entity.getUser();

        return new GroupUserDto(
                entity.getId(),
                group.getId(),
                group.getName(),
                user.getId(),
                user.getUsername(),
                entity.getIsMember(),
                entity.getIsCreator()
        );
    }
}
