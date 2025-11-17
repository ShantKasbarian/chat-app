package org.chat.service;

import org.chat.model.TokenDto;

public interface AuthenticationService {
    TokenDto login(String username, String password);
    TokenDto createUser(String username, String password);
}
