package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.repository.ContactRepository;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class ContactRepositoryImpl implements ContactRepository {
    private final static String USER_ID_FIELD = "userId";

    private final static String TARGET_USER_ID_FIELD = "targetUserId";

    private static final String FIND_BY_USER_ID_AND_TARGET_USER_ID = USER_ID_FIELD + " = ?1 and " + TARGET_USER_ID_FIELD + " = ?2";

    @Override
    public PanacheQuery<Contact> getContacts(UUID id, int page, int size) {
        log.debug("fetching contacts of user with id {}", id);

        var contacts = find(USER_ID_FIELD, id)
                .page(Page.of(page, size));

        log.debug("fetched contacts of user with id {}", id);

        return contacts;
    }

    @Override
    public boolean existsByUserIdTargetUserId(UUID userId, UUID targetUserId) {
        log.debug("checking if contacts of user with id {} and target id {} exist", userId, targetUserId);

        boolean exists = count(FIND_BY_USER_ID_AND_TARGET_USER_ID, userId, targetUserId) > 0;

        log.debug("checked if contacts of user with id {} and target id {} exist", userId, targetUserId);

        return exists;
    }
}
