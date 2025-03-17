package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.MessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class MessageConverter implements
        ToModelConverter<MessageDto, Message> {

    @Override
    public MessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new MessageDto(
                entity.id,
                entity.getSender().id,
                entity.getSender().getUsername(),
                entity.getRecipient().id,
                entity.getRecipient().getUsername(),
                entity.getMessage(),
                entity.getTime().format(formatter)
        );
    }
}
