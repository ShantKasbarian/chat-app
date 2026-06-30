package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.User;
import org.chat.exception.InvalidCredentialsException;
import org.chat.exception.ResourceAlreadyExistsException;
import org.chat.model.TokenDto;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class UserServiceImpl implements UserService {
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";

    private static final String USER_WITH_GIVEN_USERNAME_EXISTS_MESSAGE = "a user with the specified username already exists";

    private final UserRepository userRepository;

    private final JwtServiceImpl jwtService;

    @Override
    public TokenDto login(String username, String password) {
        log.info("authenticating user with username {}", username);

        User user = userRepository.findByUsername(username)
                .filter(target -> BCrypt.checkpw(password, target.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        String token = jwtService.generateToken(username, String.valueOf(user.getId()));

        log.info("authenticated user with username {}", username);

        return new TokenDto(token);
    }

    @Override
    public TokenDto signup(String username, String password) {
        log.info("registering user with username {}", username);

        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistsException(USER_WITH_GIVEN_USERNAME_EXISTS_MESSAGE);
        }

        User user = new User(UUID.randomUUID(), username, BCrypt.hashpw(password, BCrypt.gensalt()));
        userRepository.persist(user);

        log.info("registered user with username {}", username);

        String token = jwtService.generateToken(username, String.valueOf(user.getId()));

        return new TokenDto(token);
    }

    @Override
    public PanacheQuery<User> findByUsername(String username, int page, int size) {
        log.info("fetching users with username {}, page {}, size {}", username, page, size);

        var users = userRepository.findByUsername(username, page, size);

        log.info("fetched users with username {}, page {}, size {}", username, page, size);

        return users;
    }
}
