package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.chat.entity.Contact;

import java.util.List;
import java.util.UUID;

public interface ContactRepository extends PanacheRepositoryBase<Contact, UUID> {
    List<Contact> getContacts(UUID id);
}
