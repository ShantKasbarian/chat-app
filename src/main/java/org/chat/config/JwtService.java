package org.chat.config;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Singleton
public class JwtService {
    public static final String USER_ID_CLAIM = "userId";

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    private String jwtIssuer;

    public String generateToken(String username, String userId) {
        return Jwt.issuer(jwtIssuer)
                .upn(username)
                .claim(USER_ID_CLAIM, userId)
                .expiresAt(Instant.now().plus(10, ChronoUnit.HOURS))
                .sign();
    }
}

