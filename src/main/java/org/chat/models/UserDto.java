package org.chat.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
        String id,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password
) {

}
