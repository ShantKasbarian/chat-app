package org.chat.model;

import java.util.UUID;

public record ContactDto(UUID id , UUID userId, String username) {
}
