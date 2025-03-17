package org.chat.models;

public record GroupMessageDto(
        String id,
        String senderId,
        String senderName,
        String message,
        String groupId,
        String groupName,
        String time
) {

}
