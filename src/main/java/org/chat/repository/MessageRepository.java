package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import org.chat.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends PanacheMongoRepositoryBase<Message, UUID> {
    List<Message> getMessages(UUID currentUserId, UUID targetUserId, int page, int size);
    List<Message> getGroupMessages(UUID groupId, int page, int size);
}
