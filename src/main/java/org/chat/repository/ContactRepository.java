package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.Contact;

import java.util.List;

public interface ContactRepository extends PanacheRepository<Contact> {
    List<Contact> getContacts(String currentUserId);
}
