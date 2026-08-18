package org.chat.model;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
    @NotBlank(message = "username must be specified") String username,
    @NotBlank(message = "password must be specified") String password) {}
