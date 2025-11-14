package org.chat.model;

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
