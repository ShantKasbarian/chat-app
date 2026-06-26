package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;
import org.chat.service.UserService;

@ApplicationScoped
@AllArgsConstructor
public class ContactConverter implements ToModelConverter<ContactDto, Contact> {
    @Override
    public ContactDto convertToModel(Contact entity) {
        return new ContactDto(
                entity.getId(),
                entity.getTargetUserId(),
                entity.getUsernameSnapshot()
        );
    }
}
