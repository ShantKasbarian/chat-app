package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.Message;

import java.util.List;

public interface MessageRepository extends PanacheRepository<Message> {
    List<Message> getMessages(String currentUserId, String recipientId, int page, int size);
    List<Message> getGroupMessages(String groupId, int page, int size);
}
