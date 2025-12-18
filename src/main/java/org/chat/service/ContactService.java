package org.chat.service;

import org.chat.entity.Contact;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    List<Contact> getContacts(UUID userId);
    Contact addContact(UUID userId, UUID targetUserId);
}
