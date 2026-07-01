package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Message;
import org.chat.model.ConversationDto;
import org.chat.model.PageDto;

import java.util.UUID;

public interface MessageRepository extends PanacheMongoRepositoryBase<Message, UUID> {
    PanacheQuery<Message> findByUserIdTargetUserId(UUID currentUserId, UUID targetUserId, int page, int size);
    PanacheQuery<Message> findByGroupId(UUID groupId, int page, int size);
    PageDto<ConversationDto> findConversations(UUID id, int page, int size);
}
