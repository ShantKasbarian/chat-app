package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.GroupMember;
import org.chat.model.GroupUserDto;

@ApplicationScoped
public class GroupMemberConverter implements ToModelConverter<GroupUserDto, GroupMember> {
    @Override
    public GroupUserDto convertToModel(GroupMember entity) {
        return new GroupUserDto(
                entity.getId(),
                entity.getGroupId(),
                entity.getUserId(),
                entity.getUsernameSnapshot(),
                entity.getRole()
        );
    }
}
