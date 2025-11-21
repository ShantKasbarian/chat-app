package org.chat.model;

import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID senderId,
        String senderUsername,
        UUID targetUserId,
        String targetUsername,
        String text,
        String time
) {

}
