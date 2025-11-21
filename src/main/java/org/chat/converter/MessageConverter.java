package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.MessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class MessageConverter implements ToModelConverter<MessageDto, Message> {

    @Override
    public MessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        User sender = entity.getSender();
        User target = entity.getTarget();

        return new MessageDto(
                entity.getId(),
                sender.getId(),
                sender.getUsername(),
                target.getId(),
                target.getUsername(),
                entity.getText(),
                entity.getTime().format(formatter)
        );
    }
}
