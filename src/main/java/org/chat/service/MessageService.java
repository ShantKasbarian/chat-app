package org.chat.service;

import org.chat.entity.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(String content, String recipientId, String currentUserId);
    List<Message> getMessages(String userId, String recipientId, int page, int size);
    Message messageGroup(String content, String groupId, String senderId);
    List<Message> getGroupMessages(String groupId, String userId, int page, int size);
}
