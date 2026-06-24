package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import org.chat.entity.Contact;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends PanacheMongoRepositoryBase<Contact, UUID> {
    List<Contact> getContacts(UUID id);
    boolean existsByUserIdTargetUserId(UUID userId, UUID targetUserId);
}
