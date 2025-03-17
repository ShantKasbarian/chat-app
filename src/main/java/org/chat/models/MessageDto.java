package org.chat.models;

public record MessageDto(
        Integer messageId,
        Integer senderId,
        String senderUsername,
        String message,
        String time
) {

}
