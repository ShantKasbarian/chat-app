package org.chat.security;

import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String username
) {
}
