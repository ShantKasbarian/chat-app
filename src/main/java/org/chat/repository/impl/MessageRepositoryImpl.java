package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Message;
import org.chat.repository.MessageRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class MessageRepositoryImpl implements MessageRepository {
    private static final String SENDER_ID_PARAMETER = "senderId";

    private static final String TARGET_USER_ID_PARAMETER = "targetUserId";

    private static final String GROUP_ID_PARAMETER = "groupId";

    private static final String GET_CURRENT_TARGET_USERS_MESSAGES = "FROM Message m WHERE m.sender.id =:" + SENDER_ID_PARAMETER +" AND m.target.id =:" + TARGET_USER_ID_PARAMETER +" or m.sender.id =:" + TARGET_USER_ID_PARAMETER + " AND m.target.id =:" + SENDER_ID_PARAMETER + " ORDER BY m.time DESC";

    private static final String GET_GROUP_MESSAGES = "FROM Message m WHERE m.group.id = :" + GROUP_ID_PARAMETER + " ORDER BY m.time DESC";

    private final EntityManager entityManager;

    @Override
    public List<Message> getMessages(UUID currentUserId, UUID targetUserId, int page, int size) {
        log.debug("fetching user with id {} target user with id {} messages", currentUserId, targetUserId);

        int offset = (page - 1) * size;

        var messages = entityManager
                .createQuery(GET_CURRENT_TARGET_USERS_MESSAGES, Message.class)
                .setParameter(SENDER_ID_PARAMETER, currentUserId)
                .setParameter(TARGET_USER_ID_PARAMETER, targetUserId)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        log.debug("fetched user with id {} target user with id {} messages", currentUserId, targetUserId);

        return messages;
    }

    @Override
    public List<Message> getGroupMessages(UUID groupId, int page, int size) {
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
