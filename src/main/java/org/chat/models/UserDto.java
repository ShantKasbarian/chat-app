package org.chat.models;

public record UserDto(
        Long id,
        String username,
        String password
) {

}
