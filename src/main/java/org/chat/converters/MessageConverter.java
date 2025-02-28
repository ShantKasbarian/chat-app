package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.MessageDto;

@ApplicationScoped
public class MessageConverter implements Converter<Message, MessageDto> {
    @Override
    public Message convertToEntity(MessageDto model) {
        Message entity = new Message();
        entity.setId(model.getId());
        entity.setMessage(model.getMessage());
        entity.setSenderId(model.getSenderId());
        entity.setRecipient(model.getReceiverId());

        return entity;
    }

    @Override
    public MessageDto convertToModel(Message entity) {
        MessageDto model = new MessageDto();
        model.setId(entity.getId());
        model.setMessage(entity.getMessage());
        model.setSenderId(entity.getSenderId());
        model.setReceiverId(entity.getRecipient());

        return model;
    }
}
