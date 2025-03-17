package org.chat.models;

public record GroupMessageDto(
        int id,
        int senderId,
        String senderName,
        String message,
        int groupId,
        String groupName,
        String time
) {

}
