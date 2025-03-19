package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.chat.config.JwtService;
import org.chat.entities.User;
import org.chat.exceptions.InvalidCredentialsException;
import org.chat.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class LoginSignupService {
    private final UserRepository userRepository;

    private final JwtService jwtService;

    public LoginSignupService(
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return jwtService.generateToken(username, user.getId());
    }

    @Transactional
    public String createUser(String username, String password) {
        if (
                userRepository.find("username", username).firstResult() != null ||
                (
                        username.length() < 5 ||
                        username.length() > 20
                )
                ||
                username.trim().contains(" ")
        ) {
            throw new InvalidCredentialsException("Invalid username");
        }

        if (password == null || !isPasswordValid(password)) {
            throw new InvalidCredentialsException("Invalid password");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

        userRepository.persist(user);

        return "user successfully registered";
    }

    private boolean isPasswordValid(String password) {
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Pattern numberPattern = Pattern.compile("[0-9]");
        Pattern specialCharacterPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>_\\-+]");

        Matcher uppercaseMatcher = uppercasePattern.matcher(password);
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);
        Matcher numberMatcher = numberPattern.matcher(password);
        Matcher specialCharacterMatcher = specialCharacterPattern.matcher(password);

        return uppercaseMatcher.find() &&
                lowercaseMatcher.find() &&
                numberMatcher.find() &&
                specialCharacterMatcher.find();
    }
}
