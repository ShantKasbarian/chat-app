package org.chat.model;

import org.chat.entity.Message;

import java.time.Instant;
import java.util.UUID;

public record ConversationDto(
        UUID id,
        String name,
        String message,
        Message.Type messageType,
        Instant time
) {
}
