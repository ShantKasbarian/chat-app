package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Message;
import org.chat.model.MessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class MessageConverter implements ToModelConverter<MessageDto, Message> {

    @Override
    public MessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new MessageDto(
                entity.getId(),
                entity.getSender().getId(),
                entity.getSender().getUsername(),
                entity.getRecipient().getId(),
                entity.getRecipient().getUsername(),
                entity.getText(),
                entity.getTime().format(formatter)
        );
    }
}
