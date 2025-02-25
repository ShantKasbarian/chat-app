package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.User;
import org.chat.models.UserDto;

@ApplicationScoped
public class UserConverter implements Converter<User, UserDto> {
    @Override
    public User convertToEntity(UserDto model) {
        User entity = new User();
        entity.setId(model.getId());
        entity.setUsername(model.getUsername());
        entity.setPassword(model.getPassword());

        return entity;
    }

    @Override
    public UserDto convertToModel(User entity) {
        UserDto model = new UserDto();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setPassword(entity.getPassword());

        return model;
    }
}
