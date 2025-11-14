package org.chat.service;

public interface AuthenticationService {
    String login(String username, String password);
    String createUser(String username, String password);
}
