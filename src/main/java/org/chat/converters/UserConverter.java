package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.User;
import org.chat.models.UserDto;

@ApplicationScoped
public class UserConverter implements ToEntityConverter<User, UserDto> {
    @Override
    public User convertToEntity(UserDto model) {
        User entity = new User();
        entity.setId(model.id());
        entity.setUsername(model.username());
        entity.setPassword(model.password());

        return entity;
    }
}
