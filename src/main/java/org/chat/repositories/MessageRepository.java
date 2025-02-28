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

    public List getMessages(int currentUserId, int receiverId) {
        Query query1 =
                entityManager
                    .createNativeQuery(
                            "select u.username, m.message from messages m " +
                                    "left join users u on u.id = m.sender_id "+
                                    "where m.recipient_id = ? and m.sender_id = ?",
                            GroupMessageDto.class
                    )
                    .setParameter(1, receiverId)
                    .setParameter(2, currentUserId);

        Query query2 =
                entityManager
                        .createNativeQuery(
                            "select u.username, m.message from messages m " +
                                    "left join users u on u.id = m.sender_id "+
                                    "where m.recipient_id = ? and m.sender_id = ?",
                        GroupMessageDto.class
                )
                .setParameter(1, currentUserId)
                .setParameter(2, receiverId);

        return List.of(query1.getResultList(), query2.getResultList());

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
        entityManager.createQuery(
                "insert into Message (message, senderId, recipient) values (:message, :sender, :recipient)"
            )
            .setParameter("message", message.getMessage())
            .setParameter("sender", message.getSenderId())
            .setParameter("recipient", message.getRecipient())
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
