package org.chat.converter;

public interface ToEntityConverter<T, M> {
    T convertToEntity(M model);
}
