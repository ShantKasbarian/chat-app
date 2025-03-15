package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.SubmitMessageDto;

@ApplicationScoped
public class MessageConverter implements Converter<Message, SubmitMessageDto> {
    @Override
    public Message convertToEntity(SubmitMessageDto model) {
        Message entity = new Message();
        entity.setId(model.getId());
        entity.setMessage(model.getMessage());
        entity.setTime(model.getTime());
        return entity;
    }

    @Override
    public SubmitMessageDto convertToModel(Message entity) {
        SubmitMessageDto model = new SubmitMessageDto();
        model.setId(entity.getId());
        model.setMessage(entity.getMessage());
        model.setTime(entity.getTime());
        return model;
    }
}
