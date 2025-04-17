package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Contact;
import org.chat.models.ContactDto;

@ApplicationScoped
public class ContactConverter implements ToModelConverter<ContactDto, Contact> {
    @Override
    public ContactDto convertToModel(Contact entity) {
        return new ContactDto(
                entity.getId(),
                entity.getContact().getId(),
                entity.getContact().getUsername()
        );
    }
}
