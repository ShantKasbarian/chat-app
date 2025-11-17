package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.User;
import org.chat.model.UserDto;

@ApplicationScoped
public class UserConverter implements ToEntityConverter<User, UserDto>, ToModelConverter<UserDto, User> {
    @Override
    public User convertToEntity(UserDto model) {
        User entity = new User();
        entity.setUsername(model.username());
        entity.setPassword(model.password());

        return entity;
    }

    @Override
    public UserDto convertToModel(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                null
        );
    }
}
