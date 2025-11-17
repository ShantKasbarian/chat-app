package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.User;
import org.chat.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    private static final String ID_PARAMETER = "id";

    private static final String USERNAME_PARAMETER = "username";

    private static final String PATTERN_PARAMETER = "pattern";

    private static final String SEARCH_BY_USERNAME = "FROM User u WHERE UPPER(u.username) LIKE UPPER(:" + PATTERN_PARAMETER + ")";

    private final EntityManager entityManager;

    @Override
    public Optional<User> findById(String id) {
        log.debug("fetching user with id {}", id);

        Optional<User> user = find(ID_PARAMETER, id).firstResultOptional();

        log.debug("fetched user with id {}", id);

        return user;
    }

    @Override
    public boolean existsById(String id) {
        log.debug("checking if user with id {} exists", id);

        boolean exists = count(ID_PARAMETER, id) > 0;

        log.debug("checked if user with id {} exists", id);

        return exists;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("fetching user with username {}", username);

        Optional<User> user = find(USERNAME_PARAMETER,username).firstResultOptional();

        log.debug("fetched user with username {}", username);

        return user;
    }

    @Override
    public List<User> searchByUsername(String username) {
        log.debug("fetching users with username {}", username);

        var users = entityManager
                .createQuery(SEARCH_BY_USERNAME, User.class)
                .setParameter(PATTERN_PARAMETER, "%" + username + "%")
                .getResultList();

        log.debug("fetched users with username {}", username);

        return users;
    }
}
