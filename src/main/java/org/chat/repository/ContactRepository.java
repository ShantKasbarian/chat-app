package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Contact;

import java.util.UUID;

public interface ContactRepository extends PanacheMongoRepositoryBase<Contact, UUID> {
    PanacheQuery<Contact> findByUserId(UUID id, int page, int size);
    boolean existsByUserIdTargetUserId(UUID userId, UUID targetUserId);
}
