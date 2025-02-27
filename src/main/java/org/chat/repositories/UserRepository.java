package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.chat.entities.User;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User findByUsername(String username) {
        try {
            return find("username",username).firstResult();
        }
        catch (Exception e) {
            return null;
        }
    }

    public List<String> getContacts(int currentUserId) {
        Query query = entityManager.createNativeQuery("select u.username from users u" +
                " left join contacts c on c.contact_id = u.id where user_id = ?");
        query.setParameter(1, currentUserId);

        return query.getResultList();
    }

    @Transactional
    public void addContact(int userId, int contactId) {
        entityManager.createNativeQuery("insert into contacts(user_id, contact_id)" +
                " values(?, ?)"
        )
        .setParameter(1, userId)
        .setParameter(2, contactId)
        .executeUpdate();
    }
}
