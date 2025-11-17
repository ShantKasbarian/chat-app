package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.User;
import org.chat.exception.ResourceNotFoundException;
import org.chat.repository.UserRepository;

import java.util.List;
@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    private static final String ID_PARAMETER = "id";

    private static final String USERNAME_PARAMETER = "username";

    private static final String PATTERN_PARAMETER = "pattern";

    private static final String SEARCH_BY_USERNAME = "FROM User u WHERE UPPER(u.username) LIKE UPPER(:" + PATTERN_PARAMETER + ")";

    private static final String EXISTS_BY_ID_QUERY = "SELECT COUNT(u) = 1 FROM USER u WHERE u.id = :" + ID_PARAMETER;

    private final EntityManager entityManager;

    @Override
    public User findById(String id) {
        log.debug("fetching user with id {}", id);

        User user = find(ID_PARAMETER, id).firstResult();

        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        log.debug("fetched user with id {}", id);

        return user;
    }

    @Override
    public boolean existsById(String id) {
        log.debug("checking if user with id {} exists", id);

        boolean exists = entityManager
                .createQuery(EXISTS_BY_ID_QUERY, Boolean.class)
                .setParameter(ID_PARAMETER, id)
                .getSingleResult();

        log.debug("checked if user with id {} exists", id);

        return exists;
    }

    @Override
    public User findByUsername(String username) {
        log.debug("fetching user with username {}", username);

        User user = find(USERNAME_PARAMETER,username).firstResult();

        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

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
