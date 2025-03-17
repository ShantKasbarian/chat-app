package org.chat.models;

public record UserDto(
        int id,
        String username,
        String password
) {

}
