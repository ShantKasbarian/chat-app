package org.chat.model;

import java.util.List;

public record ErrorResponseDto(
        String error,
        List<FieldViolationDto> violations
) {
}
