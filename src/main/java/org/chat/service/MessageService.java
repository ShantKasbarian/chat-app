package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(String content, UUID targetUserId, UUID currentUserId, String currentUsername);
    PanacheQuery<Message> getMessages(UUID userId, UUID targetUserId, int page, int size);
    Message messageGroup(String content, UUID groupId, UUID senderId, String senderUsername);
    PanacheQuery<Message> getGroupMessages(UUID groupId, UUID userId, int page, int size);
}
