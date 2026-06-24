package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
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

    private final static String TARGET_USER_ID_PARAMETER = "targetUserId";

    private final static String GET_USER_CONTACTS = "FROM Contact c WHERE c.user.id = :" + USER_ID_PARAMETER;

    private final static String EXISTS_BY_USER_ID_TARGET_USER_ID = "SELECT COUNT(c) > 0 FROM Contact c WHERE c.user.id = :" + USER_ID_PARAMETER + " AND c.target.id = :" + TARGET_USER_ID_PARAMETER;

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

    @Override
    public boolean existsByUserIdTargetUserId(UUID userId, UUID targetUserId) {
        log.debug("checking if contacts of user with id {} and target id {} exist", userId, targetUserId);

        boolean exists = entityManager.createQuery(EXISTS_BY_USER_ID_TARGET_USER_ID, Boolean.class)
                .setParameter(USER_ID_PARAMETER, userId)
                .setParameter(TARGET_USER_ID_PARAMETER, targetUserId)
                .getSingleResult();

        log.debug("checked if contacts of user with id {} and target id {} exist", userId, targetUserId);

        return exists;
    }
}
