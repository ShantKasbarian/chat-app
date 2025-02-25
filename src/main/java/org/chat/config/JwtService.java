package org.chat.config;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;

@Singleton
public class JwtService {
    public String generateJwtToken() {
        return Jwt.issuer("chat-app")
                .subject("chat-app")
                .expiresAt(System.currentTimeMillis() + 5 * 60 * 60)
                .sign();
    }
}
