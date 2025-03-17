package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.chat.entities.Contact;

import java.util.List;

@ApplicationScoped
public class ContactRepository implements PanacheRepository<Contact> {
    private final EntityManager entityManager;

    public ContactRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Contact> getContacts(Long currentUserId) {
        return entityManager.createQuery(
                "from Contact c where c.user.id = :userId",
                Contact.class
        )
        .setParameter("userId", currentUserId)
        .getResultList();
    }

    @Transactional
    public void addContact(Long userId, Long contactId) {
        entityManager.createQuery(
            "insert into Contact c (c.user.id, c.contact.id)" +
                    " values(:userId, :contactId)"
        )
        .setParameter("userId", userId)
        .setParameter("contactId", contactId)
        .executeUpdate();
    }
}
