package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.SubmitMessageDto;

@ApplicationScoped
public class MessageConverter implements Converter<Message, SubmitMessageDto> {
    @Override
    public Message convertToEntity(SubmitMessageDto model) {
        Message entity = new Message();
        entity.setId(model.id());
        entity.setMessage(model.message());
        entity.setTime(model.time());
        return entity;
    }

    @Override
    public SubmitMessageDto convertToModel(Message entity) {
        return new SubmitMessageDto(
            entity.getId(),
            entity.getMessage(),
            entity.getRecipient().getUsername(),
            entity.getGroup().getName(),
            entity.getTime()
        );
    }
}
