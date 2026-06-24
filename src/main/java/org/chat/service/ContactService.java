package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Contact;

import java.util.UUID;

public interface ContactService {
    PanacheQuery<Contact> findByUserId(UUID userId, int page, int size);
    Contact addContact(UUID userId, UUID targetUserId);
}
