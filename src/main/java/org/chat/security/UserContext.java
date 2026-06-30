package org.chat.security;

import jakarta.enterprise.context.RequestScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

import static org.chat.service.impl.JwtServiceImpl.USER_ID_CLAIM;

@RequiredArgsConstructor
@RequestScoped
public class UserContext {
    private final JsonWebToken jsonWebToken;

    public UserPrincipal get() {
        return new UserPrincipal(
                UUID.fromString(jsonWebToken.getClaim(USER_ID_CLAIM)),
                jsonWebToken.getName()
        );
    }
}
