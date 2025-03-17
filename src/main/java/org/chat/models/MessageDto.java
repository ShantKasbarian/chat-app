package org.chat.models;

public record MessageDto(
        String messageId,
        String senderId,
        String senderUsername,
        String recipientId,
        String recipientUsername,
        String message,
        String time
) {

}
