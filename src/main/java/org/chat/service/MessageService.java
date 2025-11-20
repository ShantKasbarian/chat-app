package org.chat.service;

import org.chat.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(String content, UUID recipientId, UUID currentUserId);
    List<Message> getMessages(UUID userId, UUID recipientId, int page, int size);
    Message messageGroup(String content, UUID groupId, UUID senderId);
    List<Message> getGroupMessages(UUID groupId, UUID userId, int page, int size);
}
