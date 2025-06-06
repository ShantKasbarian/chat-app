package org.chat.converters;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.models.GroupMessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class GroupMessageConverter implements
        ToModelConverter<GroupMessageDto, Message> {

    @Override
    public GroupMessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new GroupMessageDto(
                entity.getId(),
                entity.getSender().getId(),
                entity.getSender().getName(),
                entity.getMessage(),
                entity.getGroup().getId(),
                entity.getGroup().getName(),
                entity.getTime().format(formatter)
        );
    }
}
