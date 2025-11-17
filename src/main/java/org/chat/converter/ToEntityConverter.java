package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ToEntityConverter<T, M> {
    T convertToEntity(M model);
}
