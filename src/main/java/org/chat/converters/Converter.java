package org.chat.converters;

public interface Converter<T, M> {
    public T convertToEntity(M model);
    public M convertToModel(T entity);
}
