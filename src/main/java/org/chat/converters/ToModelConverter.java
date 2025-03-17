package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ToModelConverter<M, T> {
    M convertToModel(T entity);
}
