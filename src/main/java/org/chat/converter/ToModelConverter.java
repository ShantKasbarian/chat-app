package org.chat.converter;

public interface ToModelConverter<M, T> {
    M convertToModel(T entity);
}
