package org.chat.repositories;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entities.Message;

import java.util.List;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class MessageRepository implements PanacheRepository<Message> {
    private static final String SENDER_ID_PARAMETER = "senderId";

    private static final String RECIPIENT_ID_PARAMETER = "recipientId";

    private static final String GROUP_ID_PARAMETER = "groupId";

    private static final String GET_CURRENT_TARGET_USERS_MESSAGES = "from Message m where m.sender.id =:" + SENDER_ID_PARAMETER +" and m.recipient.id =:" + RECIPIENT_ID_PARAMETER +" or m.sender.id =:" + RECIPIENT_ID_PARAMETER + " and m.recipient.id =:" + SENDER_ID_PARAMETER + " order by m.time DESC";

    private static final String GET_GROUP_MESSAGES = "from Message m where m.group.id = :" + GROUP_ID_PARAMETER + " order by m.time DESC";

    private final EntityManager entityManager;

    public List<Message> getMessages(String currentUserId, String recipientId, int page, int size) {
        log.debug("fetching user with id {} target user with id {} messages", currentUserId, recipientId);

        int offset = (page - 1) * size;

        var messages = entityManager
                .createQuery(GET_CURRENT_TARGET_USERS_MESSAGES, Message.class)
                .setParameter(SENDER_ID_PARAMETER, currentUserId)
                .setParameter(RECIPIENT_ID_PARAMETER, recipientId)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        log.debug("fetched user with id {} target user with id {} messages", currentUserId, recipientId);

        return messages;
    }

    public List<Message> getGroupMessages(String groupId, int page, int size) {
        log.debug("fetching messages of group with id {}", groupId);

        int offset = (page - 1) * size;

        var messages = entityManager
                .createQuery(GET_GROUP_MESSAGES, Message.class)
                .setParameter(GROUP_ID_PARAMETER, groupId)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        log.debug("fetched messages of group with id {}", groupId);

        return messages;
    }
}
