package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ToEntityConverter<T, M> {
    T convertToEntity(M model);
}
