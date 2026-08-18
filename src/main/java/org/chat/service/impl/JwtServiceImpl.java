package org.chat.service.impl;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.chat.service.JwtService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class JwtServiceImpl implements JwtService {
  public static final String USER_ID_CLAIM = "userId";

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  private String jwtIssuer;

  @Override
  public String generateToken(String username, String userId) {
    return Jwt.issuer(jwtIssuer)
        .upn(username)
        .claim(USER_ID_CLAIM, userId)
        .expiresAt(Instant.now().plus(10, ChronoUnit.HOURS))
        .sign();
  }
}
