package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.chat.entities.Message;
import org.chat.models.GroupMessageDto;

import java.util.List;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> {
    private final EntityManager entityManager;

    public MessageRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Message> getMessages(int currentUserId, int receiverId) {
        List<Message> messages =
                entityManager
                    .createQuery(
                            "from Message m where m.recepient = :recepient and m.senderId = :sender",
                            Message.class
                    )
                    .setParameter("recepient", receiverId)
                    .setParameter("sender", currentUserId)
                    .getResultList();

        List<Message> messagesList2 =
                entityManager.createQuery(
                        "from Message m where m.recepient = :recepient and m.senderId = :sender",
                        Message.class
                )
                .setParameter("recepient", currentUserId)
                .setParameter("sender", receiverId)
                .getResultList();

        if (messagesList2 != null) {
            messages.addAll(messagesList2);
        }

        return messages;
    }

    public List<GroupMessageDto> getMessages(int groupId) {
        Query query = entityManager.createNativeQuery(
            "select u.username, m.message  from messages m " +
                    "left join users u on u.id = m.sender_id " +
                    "where m.group_id = ?",
                GroupMessageDto.class
        ).setParameter(1, groupId);

        return query.getResultList();
    }

    public void save(Message message) {
        entityManager.createQuery("insert into Message (message, senderId, recepient) values (:message, :sender, :recepient)")
            .setParameter("message", message.getMessage())
            .setParameter("sender", message.getSenderId())
            .setParameter("recepient", message.getRecepient())
            .executeUpdate();
    }

    public void save(String message, int groupId, int senderId) {
        entityManager.createNativeQuery(
                "insert into messages (message, sender_id, group_id) values (?, ?, ?)")
                .setParameter(1, message)
                .setParameter(2, senderId)
                .setParameter(3, groupId)
                .executeUpdate();
    }
}
