package org.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;
import org.chat.entity.Message;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConversationDto(
    UUID id,
    String name,
    String senderUsername,
    String message,
    Message.Type messageType,
    Instant time) {}
