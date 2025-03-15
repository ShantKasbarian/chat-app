package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.MessageRepresentationDto;

@ApplicationScoped
public class MessageRepresentationConverter {
    public MessageRepresentationDto convertToMessageRepresentationDto(Message message) {
        MessageRepresentationDto messageRepresentationDto = new MessageRepresentationDto();

        messageRepresentationDto.setUsername(message.getSender().getUsername());
        messageRepresentationDto.setMessage(message.getMessage());
        messageRepresentationDto.setTime(message.getTime());

        return messageRepresentationDto;
    }
}
