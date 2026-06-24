package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.User;
import org.chat.exception.InvalidCredentialsException;
import org.chat.model.TokenDto;
import org.chat.repository.UserRepository;
import org.chat.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class UserServiceImpl implements UserService {
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";

    private static final String INVALID_USERNAME_MESSAGE = "Invalid username";

    private static final String INVALID_PASSWORD_MESSAGE = "Invalid password";

    private static final String UPPERCASE_REGEX = "[A-Z]";

    private static final String LOWERCASE_REGEX = "[a-z]";

    private static final String NUMBER_REGEX = "[0-9]";

    private static final String SPECIAL_CHARACTERS_REGEX = "[!@#$%^&*(),.?\":{}|<>_\\-+]";

    private final UserRepository userRepository;

    private final JwtServiceImpl jwtService;

    @Override
    public TokenDto login(String username, String password) {
        log.info("authenticating user with username {}", username);

        User user = userRepository.findByUsername(username)
                .filter(user1 -> BCrypt.checkpw(password, user1.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        String token = jwtService.generateToken(username, String.valueOf(user.getId()));

        log.info("authenticated user with username {}", username);

        return new TokenDto(token);
    }

    @Override
    @Transactional
    public TokenDto signUp(String username, String password) {
        log.info("registering user with username {}", username);

        if (
            userRepository.existsByUsername(username) ||
            (username.length() < 5 || username.length() > 20) ||
            username.trim().contains(" ")
        ) {
            throw new InvalidCredentialsException(INVALID_USERNAME_MESSAGE);
        }

        if (password == null || !isPasswordValid(password)) {
            throw new InvalidCredentialsException(INVALID_PASSWORD_MESSAGE);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

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

    private boolean isPasswordValid(String password) {
        Pattern uppercasePattern = Pattern.compile(UPPERCASE_REGEX);
        Pattern lowercasePattern = Pattern.compile(LOWERCASE_REGEX);
        Pattern numberPattern = Pattern.compile(NUMBER_REGEX);
        Pattern specialCharacterPattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX);

        Matcher uppercaseMatcher = uppercasePattern.matcher(password);
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);
        Matcher numberMatcher = numberPattern.matcher(password);
        Matcher specialCharacterMatcher = specialCharacterPattern.matcher(password);

        return uppercaseMatcher.find() && lowercaseMatcher.find() &&
                numberMatcher.find() && specialCharacterMatcher.find();
    }
}
