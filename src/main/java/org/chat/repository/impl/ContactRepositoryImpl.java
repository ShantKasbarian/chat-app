package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Contact;
import org.chat.repository.ContactRepository;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ContactRepositoryImpl implements ContactRepository {
    private static final String USER_ID = "userId";

    private static final String TARGET_USER_ID = "targetUserId";

    private static final String FIND_BY_USER_ID_AND_TARGET_USER_ID = USER_ID + " = ?1 and " + TARGET_USER_ID + " = ?2";

    @Override
    public PanacheQuery<Contact> findByUserId(UUID id, int page, int size) {
        log.debug("fetching contacts of user with id {}", id);

        var contacts = find(USER_ID, id)
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
