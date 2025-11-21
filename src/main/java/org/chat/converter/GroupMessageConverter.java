package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.GroupMessageDto;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class GroupMessageConverter implements ToModelConverter<GroupMessageDto, Message> {
    @Override
    public GroupMessageDto convertToModel(Message entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        User sender = entity.getSender();
        Group group = entity.getGroup();

        return new GroupMessageDto(
                entity.getId(),
                sender.getId(),
                sender.getName(),
                entity.getText(),
                group.getId(),
                group.getName(),
                entity.getTime().format(formatter)
        );
    }
}
