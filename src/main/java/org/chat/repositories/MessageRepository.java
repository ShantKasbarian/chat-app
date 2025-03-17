package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.chat.entities.Message;

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
                            "from Message m where m.sender.id =:sender and m.recipient.id =:recipient",
                            Message.class
                    )
                    .setParameter("sender", currentUserId)
                    .setParameter("recipient", receiverId)
                    .getResultList();

        List<Message> messageList2 =
                entityManager
                        .createQuery(
                            "from Message m where m.sender.id =:sender and m.recipient.id =:recipient",
                        Message.class
                )
                .setParameter("sender", receiverId)
                .setParameter("recipient", currentUserId)
                .getResultList();

        messages.addAll(messageList2);

        return messages;
    }

    public List<Message> getGroupMessages(int groupId) {
        return entityManager
                .createQuery(
                        "from Message m where m.group.id = :groupId",
                        Message.class
                )
                .setParameter("groupId", groupId)
                .getResultList();
    }

    public void save(Message message) {
        entityManager.createQuery(
                "insert into Message m (m.message, m.sender, m.recipient, m.time) values (:message, :sender, :recipient, :time)"
            )
            .setParameter("message", message.getMessage())
            .setParameter("sender", message.getSender())
            .setParameter("recipient", message.getRecipient())
            .setParameter("time", message.getTime())
            .executeUpdate();
    }

    public void saveGroupMessage(Message message) {
        entityManager.createQuery(
                "insert into Message m (m.message, m.sender, m.group, m.time) values (:message, :senderId, :groupId, :time)")
                .setParameter("message", message.getMessage())
                .setParameter("senderId", message.getSender())
                .setParameter("groupId", message.getGroup())
                .setParameter("time", message.getTime())
                .executeUpdate();
    }
}
