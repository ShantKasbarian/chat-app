package org.chat.models;

import java.time.LocalDateTime;

public record SubmitMessageDto(
        int id,
        String message,
        String receiverUsername,
        String groupName,
        LocalDateTime time
) {

}
