package org.chat.model;

import java.util.UUID;

public record GroupMessageDto(
        UUID id,
        UUID senderId,
        String senderName,
        String message,
        UUID groupId,
        String groupName,
        String time
) {

}
