package org.chat.model;

import java.util.UUID;

public record GroupMessageDto(
        UUID id,
        UUID senderId,
        String senderUsername,
        String text,
        UUID groupId,
        String groupName,
        String time
) {

}
