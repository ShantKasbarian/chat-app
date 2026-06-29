package org.chat.converter;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entity.Message;
import org.chat.model.GroupMessageDto;

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
