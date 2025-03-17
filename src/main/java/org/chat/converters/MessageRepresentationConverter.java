package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.MessageRepresentationDto;

@ApplicationScoped
public class MessageRepresentationConverter implements Converter<Message, MessageRepresentationDto> {
    @Override
    public Message convertToEntity(MessageRepresentationDto model) {
        Message message = new Message();
        message.setMessage(model.message());

        return message;
    }

    @Override
    public MessageRepresentationDto convertToModel(Message message) {
        return new MessageRepresentationDto(
            message.getSender().getUsername(),
            message.getMessage(),
            message.getTime()
        );
    }
}
