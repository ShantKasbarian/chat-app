package org.chat.models;

public record GroupUserDto(
        String id,
        String groupId,
        String groupName,
        String userId,
        String username,
        boolean isMember,
        boolean isCreator
) {
}
