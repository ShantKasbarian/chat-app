package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Message;
import org.chat.repository.MessageRepository;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class MessageRepositoryImpl implements MessageRepository {
    private static final String SENDER_ID = "senderId";

    private static final String TARGET_USER_ID = "targetUserId";

    private static final String GROUP_ID = "groupId";

    private static final String USERS_MESSAGE_QUERY = "(" + SENDER_ID + " = ?1 AND " + TARGET_USER_ID + " = ?2) OR (" + SENDER_ID + " = ?2 AND " + TARGET_USER_ID + " = ?1)";

    @Override
    public PanacheQuery<Message> findByUserIdTargetUserId(UUID currentUserId, UUID targetUserId, int page, int size) {
        log.debug("fetching user with id {} target user with id {} messages", currentUserId, targetUserId);

        var messages = find(USERS_MESSAGE_QUERY, currentUserId, targetUserId)
                .page(Page.of(page, size));

        log.debug("fetched user with id {} target user with id {} messages", currentUserId, targetUserId);

        return messages;
    }

    @Override
    public PanacheQuery<Message> findByGroupId(UUID groupId, int page, int size) {
        log.debug("fetching messages of group with id {}", groupId);

        var messages = find(GROUP_ID, groupId)
                .page(Page.of(page, size));

        log.debug("fetched messages of group with id {}", groupId);

        return messages;
    }
}
