package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.repository.ContactRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class ContactRepositoryImpl implements ContactRepository {
    private final static String USER_ID_PARAMETER = "userId";

    private final static String GET_USER_CONTACTS = "FROM Contact c WHERE c.user.id = :" + USER_ID_PARAMETER;

    private final EntityManager entityManager;

    @Override
    public List<Contact> getContacts(UUID id) {
        log.debug("fetching contacts of user with id {}", id);

        var contacts = entityManager.createQuery(GET_USER_CONTACTS, Contact.class)
                .setParameter(USER_ID_PARAMETER, id)
                .getResultList();

        log.debug("fetched contacts of user with id {}", id);

        return contacts;
    }
}
