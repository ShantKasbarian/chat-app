package org.chat.service;

public interface JwtService {
  String generateToken(String username, String userId);
}
