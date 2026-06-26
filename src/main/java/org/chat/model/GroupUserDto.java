package org.chat.model;

import org.chat.entity.GroupUser;

import java.util.UUID;

public record GroupUserDto(
        UUID id,
        UUID groupId,
        UUID userId,
        String username,
        GroupUser.Role role
) {
}
