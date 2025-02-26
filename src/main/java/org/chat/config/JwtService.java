package org.chat.config;

import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;

@Singleton
public class JwtService {

    public String generateJwtToken(String username, int userId) {
        return Jwt.issuer("http://localhost:8000")
                .upn(username)
                .claim("id", userId)
                .expiresAt(System.currentTimeMillis() + 60000 * 60)
                .jws()
                .algorithm(SignatureAlgorithm.RS256)
                .sign();
    }
}

