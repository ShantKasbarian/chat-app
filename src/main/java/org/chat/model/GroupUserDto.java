package org.chat.model;

import java.util.UUID;

public record GroupUserDto(
        UUID id,
        UUID groupId,
        String groupName,
        UUID userId,
        String username,
        boolean isMember,
        boolean isCreator
) {
}
