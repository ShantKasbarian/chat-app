package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Message;

import java.util.UUID;

public interface MessageRepository extends PanacheMongoRepositoryBase<Message, UUID> {
    PanacheQuery<Message> getMessages(UUID currentUserId, UUID targetUserId, int page, int size);
    PanacheQuery<Message> getGroupMessages(UUID groupId, int page, int size);
}
