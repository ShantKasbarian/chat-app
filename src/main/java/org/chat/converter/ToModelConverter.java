package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ToModelConverter<M, T> {
    M convertToModel(T entity);
}
