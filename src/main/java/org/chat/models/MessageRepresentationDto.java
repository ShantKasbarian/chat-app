package org.chat.models;

import java.time.LocalDateTime;

public record MessageRepresentationDto(
        String username,
        String message,
        LocalDateTime time
) {

}
