package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.chat.entities.User;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findById(int id) {
        return entityManager.find(User.class, id);
    }

    public User findByUsername(String username) {
        return find("username",username).firstResult();
    }

    public List getContacts(int currentUserId) {
        return entityManager
                .createNativeQuery(
                        "select u.username from users u " +
                        "left join users c on u.id = c.contact_id " +
                        "where u.user_id = ? ",
                        String.class
                )
                .setParameter(1, currentUserId)
                .getResultList();
    }

    public void addContact(int userId, String username) {
        User contact = findByUsername(username);

        if (contact == null) {
            throw new RuntimeException("Contact not found");
        }

        entityManager.createNativeQuery("insert into contacts(user_id, contact_id)" +
                " values(?, ?)"
        )
        .setParameter(1, userId)
        .setParameter(2, contact.getId());
    }
}
