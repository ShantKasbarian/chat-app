package org.chat.model;

import java.util.UUID;

public record MessageDto(
        UUID messageId,
        UUID senderId,
        String senderUsername,
        UUID recipientId,
        String recipientUsername,
        String message,
        String time
) {

}
