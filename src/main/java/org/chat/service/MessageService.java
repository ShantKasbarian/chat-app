package org.chat.service;

import org.chat.entity.Message;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;

import java.util.List;

public interface MessageService {
    Message writeMessage(String content, String recipientId, String currentUserId);
    List<MessageDto> getMessages(String userId, String recipientId, int page, int size);
    Message messageGroup(String content, String groupId, String senderId);
    List<GroupMessageDto> getGroupMessages(String groupId, String userId, int page, int size);
}
