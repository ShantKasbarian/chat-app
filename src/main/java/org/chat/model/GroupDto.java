package org.chat.model;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record GroupDto(
        UUID id,
        @NotBlank(message = "group name must be specified") String name
) {
}
