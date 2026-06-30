package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.GroupMember;
import org.chat.model.GroupMemberDto;

@ApplicationScoped
public class GroupMemberConverter implements ToModelConverter<GroupMemberDto, GroupMember> {
    @Override
    public GroupMemberDto convertToModel(GroupMember entity) {
        return new GroupMemberDto(
                entity.getId(),
                entity.getGroupId(),
                entity.getUserId(),
                entity.getUsernameSnapshot(),
                entity.getRole()
        );
    }
}
