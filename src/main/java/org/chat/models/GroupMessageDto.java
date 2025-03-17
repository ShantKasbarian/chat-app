package org.chat.models;

public record GroupMessageDto(
        Long id,
        Long senderId,
        String senderName,
        String message,
        Long groupId,
        String groupName,
        String time
) {

}
