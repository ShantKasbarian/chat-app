package org.chat.model;

import java.util.UUID;

public record GroupDto(
        UUID id,
        String name
) {
}
