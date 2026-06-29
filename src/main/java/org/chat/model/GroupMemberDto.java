package org.chat.model;

import org.chat.entity.GroupMember;

import java.util.UUID;

public record GroupMemberDto(
        UUID id,
        UUID groupId,
        UUID userId,
        String username,
        GroupMember.Role role
) {
}
