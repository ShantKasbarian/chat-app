package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface Converter<T, M> {
    public T convertToEntity(M model);
    public M convertToModel(T entity);
}
