package org.chat.models;

public record MessageDto(
        Integer messageId,
        Integer senderId,
        String senderUsername,
        Integer recipientId,
        String recipientUsername,
        String message,
        String time
) {

}
