package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Message;
import org.chat.model.ConversationDto;
import org.chat.model.PageDto;
import org.chat.security.UserPrincipal;

import java.util.UUID;

public interface MessageService {
    Message sendMessage(UserPrincipal userPrincipal, String content, UUID targetUserId);
    PanacheQuery<Message> getMessages(UUID userId, UUID targetUserId, int page, int size);
    Message messageGroup(UserPrincipal userPrincipal, String content, UUID groupId);
    PanacheQuery<Message> getGroupMessages(UUID groupId, UUID userId, int page, int size);
    PageDto<ConversationDto> findConversations(UUID id, int page, int size);
}
