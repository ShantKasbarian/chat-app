package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Message;
import org.chat.model.GroupMessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class GroupMessageConverter implements ToModelConverter<GroupMessageDto, Message> {

    @Override
    public GroupMessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new GroupMessageDto(
                entity.getId(),
                entity.getSender().getId(),
                entity.getSender().getName(),
                entity.getText(),
                entity.getGroup().getId(),
                entity.getGroup().getName(),
                entity.getTime().format(formatter)
        );
    }
}
