package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.chat.entities.User;
import org.chat.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findById(String id) {
        User user = find("id", id).firstResult();

        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        return user;
    }

    public User findByUsername(String username) {
        User user = find("username",username).firstResult();

        if (user == null) {
            throw new ResourceNotFoundException("user not found");
        }

        return user;
    }

    public List<User> searchByUsername(String username) {
        return entityManager
                .createQuery(
                        "from User u where upper(u.username) LIKE upper(:pattern)",
                        User.class
                )
                .setParameter("pattern", "%" + username + "%")
                .getResultList();
    }
}
