package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.repository.ContactRepository;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> searchUserByUsername(String username) {
        log.info("fetching users with username {}", username);

        var users = userRepository.searchByUsername(username);

        log.info("fetched users with username {}", username);

        return users;
    }
}
