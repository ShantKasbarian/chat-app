package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import org.chat.entity.Group;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.model.GroupMessageDto;
import org.chat.service.GroupService;
import org.chat.service.UserService;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@ApplicationScoped
public class GroupMessageConverter implements ToModelConverter<GroupMessageDto, Message> {
    @Override
    public GroupMessageDto convertToModel(Message entity) {
        return new GroupMessageDto(
                entity.getId(),
                entity.getSenderId(),
                entity.getSenderUsernameSnapshot(),
                entity.getText(),
                entity.getGroupId(),
                entity.getTime()
        );
    }
}
