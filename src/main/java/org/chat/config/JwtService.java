package org.chat.config;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Singleton
public class JwtService {
    public String generateToken(String username, String userId) {
        return Jwt.issuer("http://localhost:8000")
                .upn(username)
                .claim("userId", userId)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign();
    }
}

