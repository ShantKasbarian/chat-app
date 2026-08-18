package org.chat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID senderId,
    String senderUsername,
    @NotNull(message = "targetUserId must be specified") UUID targetUserId,
    String targetUsername,
    @NotBlank(message = "text must be specified") String text,
    Instant time) {}
