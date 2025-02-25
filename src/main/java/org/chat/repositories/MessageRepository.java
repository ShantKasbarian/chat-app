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

    public List<Message> getMessages(int currentUserId, String receiverUsername) {
        return entityManager
                .createQuery(
                        "from Message where recepient = :recepient and senderId = :sender",
                        Message.class
                )
                .setParameter("recepient", receiverUsername)
                .setParameter("sender", currentUserId)
                .getResultList();
    }

    public Message writeMessage(int currentUserId, Message message, int groupId) {
        return null;
    }
}
