package org.chat.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

import static org.chat.service.impl.JwtServiceImpl.USER_ID_CLAIM;

@AllArgsConstructor
@ApplicationScoped
public class UserContext {
    private static final String UPN_CLAIM = "upn";

    private final SecurityIdentity identity;

    public UserPrincipal get() {
        return new UserPrincipal(
                identity.getAttribute(USER_ID_CLAIM),
                identity.getAttribute(UPN_CLAIM)
        );
    }
}
