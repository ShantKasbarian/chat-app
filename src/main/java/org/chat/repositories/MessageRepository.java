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

    public List<Message> getMessages(String currentUserId, String receiverId, int page, int size) {
        int offset = (page - 1) * size;

        return entityManager
            .createQuery(
                    "from Message m where m.sender.id =:sender and m.recipient.id =:recipient or m.sender.id =:recipient and m.recipient.id =:sender order by m.time DESC",
                    Message.class
            )
            .setParameter("sender", currentUserId)
            .setParameter("recipient", receiverId)
            .setFirstResult(offset)
            .setMaxResults(size)
            .getResultList();
    }

        public List<Message> getGroupMessages(String groupId, int page, int size) {
        int offset = (page - 1) * size;

        return entityManager
                .createQuery(
                        "from Message m where m.group.id = :groupId order by m.time DESC",
                        Message.class
                )
                .setParameter("groupId", groupId)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }
}
