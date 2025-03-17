package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.User;
import org.chat.models.UserDto;

@ApplicationScoped
public class UserConverter implements Converter<User, UserDto> {
    @Override
    public User convertToEntity(UserDto model) {
        User entity = new User();
        entity.setId(model.id());
        entity.setUsername(model.username());
        entity.setPassword(model.password());

        return entity;
    }

    @Override
    public UserDto convertToModel(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword()
        );
    }
}
