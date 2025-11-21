package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.chat.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends PanacheRepositoryBase<Message, UUID> {
    List<Message> getMessages(UUID currentUserId, UUID targetUserId, int page, int size);
    List<Message> getGroupMessages(UUID groupId, int page, int size);
}
