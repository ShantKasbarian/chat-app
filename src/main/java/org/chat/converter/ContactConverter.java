package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;

@ApplicationScoped
public class ContactConverter implements ToModelConverter<ContactDto, Contact> {
    @Override
    public ContactDto convertToModel(Contact entity) {
        User target = entity.getTarget();

        return new ContactDto(
                entity.getId(),
                target.getId(),
                target.getUsername()
        );
    }
}
