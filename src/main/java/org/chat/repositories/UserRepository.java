package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entities.User;
import org.chat.exceptions.ResourceNotFoundException;

import java.util.List;
@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private static final String ID_PARAMETER = "id";

    private static final String USERNAME_PARAMETER = "username";

    private static final String PATTERN_PARAMETER = "pattern";

    private static final String SEARCH_BY_USERNAME = "from User u where upper(u.username) LIKE upper(:" + PATTERN_PARAMETER + ")";

    private final EntityManager entityManager;

    public User findById(String id) {
        log.debug("fetching user with id {}", id);

        User user = find(ID_PARAMETER, id).firstResult();

        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        log.debug("fetched user with id {}", id);

        return user;
    }

    public User findByUsername(String username) {
        log.debug("fetching user with username {}", username);

        User user = find(USERNAME_PARAMETER,username).firstResult();

        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        log.debug("fetched user with username {}", username);

        return user;
    }

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
