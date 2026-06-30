package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.User;
import org.chat.repository.UserRepository;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    private static final String FIND_BY_USERNAME_MATCHES = "{ 'username': { $regex: ?1, $options: 'i' } }";

    private static final String USERNAME = "username";

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("fetching user with username {}", username);

        Optional<User> user = find(USERNAME, username).firstResultOptional();

        log.debug("fetched user with username {}", username);

        return user;
    }

    @Override
    public PanacheQuery<User> findByUsername(String username, int page, int size) {
        log.debug("fetching users with username {}, page {}, size {}", username, page, size);

        var query = find(FIND_BY_USERNAME_MATCHES, Pattern.quote(username))
                .page(Page.of(page, size));

        log.debug("fetched users with username {}, page {}, size {}", username, page, size);

        return query;
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("checking if user with username {} exists", username);

        boolean exists = count(USERNAME, username) > 0;

        log.debug("checked if user with username {} exists", username);

        return exists;
    }
}
