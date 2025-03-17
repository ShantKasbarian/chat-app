package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.MessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class MessageToMessageDtoConverter implements Converter<Message, MessageDto> {
    @Override
    public Message convertToEntity(MessageDto model) {
        Message entity = new Message();
        entity.setId(model.messageId());
        entity.setMessage(model.message());
        return entity;
    }

    @Override
    public MessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new MessageDto(
                entity.getId(),
                entity.getSender().getId(),
                entity.getSender().getUsername(),
                entity.getMessage(),
                entity.getTime().format(formatter)
        );
    }
}