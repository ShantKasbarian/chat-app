package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.User;
import org.chat.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    private static final String ID_FIELD = "id";

    private static final String USERNAME_FIELD = "username";

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("fetching user with username {}", username);

        Optional<User> user = find(USERNAME_FIELD, username).firstResultOptional();

        log.debug("fetched user with username {}", username);

        return user;
    }

    @Override
    public PanacheQuery<User> findByUsername(String username, int page, int size) {
        log.debug("fetching users with username {}, page {}, size {}", username, page, size);

        Pattern pattern = Pattern.compile(
                Pattern.quote(username),
                Pattern.CASE_INSENSITIVE
        );

        var query = find(USERNAME_FIELD, pattern)
                .page(Page.of(page, size));

        log.debug("fetched users with username {}, page {}, size {}", username, page, size);

        return query;
    }

    @Override
    public boolean existsById(UUID id) {
        log.debug("checking if user with id {} exists", id);

        boolean exists = count(ID_FIELD, id) > 0;

        log.debug("checked if user with id {} exists", id);

        return exists;
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("checking if user with username {} exists", username);

        boolean exists = count(USERNAME_FIELD, username) > 0;

        log.debug("checked if user with username {} exists", username);

        return exists;
    }
}
