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

    public List<Message> getMessages(String currentUserId, String receiverId) {
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

    public List<Message> getGroupMessages(String groupId) {
        return entityManager
                .createQuery(
                        "from Message m where m.group.id = :groupId",
                        Message.class
                )
                .setParameter("groupId", groupId)
                .getResultList();
    }
}
