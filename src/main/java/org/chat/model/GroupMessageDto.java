package org.chat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record GroupMessageDto(
    UUID id,
    UUID senderId,
    String senderUsername,
    @NotBlank(message = "text must be specified") String text,
    @NotNull(message = "groupId must be specified") UUID groupId,
    Instant time) {}
