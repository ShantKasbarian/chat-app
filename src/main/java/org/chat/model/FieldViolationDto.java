package org.chat.model;

public record FieldViolationDto(
        String field,
        String message
) {
}
