package org.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {

}
