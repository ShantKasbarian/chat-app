package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;

import java.util.List;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class ContactRepository implements PanacheRepository<Contact> {
    private final static String USER_ID_PARAMETER = "userId";

    private final static String GET_USER_CONTACTS = "from Contact c where c.user.id = :" + USER_ID_PARAMETER;

    private final EntityManager entityManager;

    public List<Contact> getContacts(String currentUserId) {
        log.debug("fetching contacts of user with id {}", currentUserId);

        var contacts = entityManager.createQuery(GET_USER_CONTACTS, Contact.class)
                .setParameter(USER_ID_PARAMETER, currentUserId)
                .getResultList();

        log.debug("fetched contacts of user with id {}", currentUserId);

        return contacts;
    }
}
