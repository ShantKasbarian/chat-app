package org.chat.model;

import java.util.List;

public record PageDto<T>(
        List<T> content,
        long totalElements,
        long totalPages
) {
}
