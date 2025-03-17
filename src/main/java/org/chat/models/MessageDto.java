package org.chat.models;

public record MessageDto(
        Long messageId,
        Long senderId,
        String senderUsername,
        Long recipientId,
        String recipientUsername,
        String message,
        String time
) {

}
