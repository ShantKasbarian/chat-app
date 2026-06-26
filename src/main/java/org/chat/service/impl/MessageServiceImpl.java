package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.GroupUser;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.UnauthorizedException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.chat.service.MessageService;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private static final String USER_NOT_FOUND = "user not found";

    private static final String GROUP_NOT_FOUND_MESSAGE = "group not found";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you are not a member of this group";

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    @Override
    @Transactional
    public Message sendMessage(String content, UUID targetUserId, UUID currentUserId, String currentUsername) {
        log.info("sending message to user with id {}", targetUserId);

        User target = userRepository.findByIdOptional(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        Message message = new Message(
                UUID.randomUUID(),
                currentUserId,
                currentUsername,
                targetUserId,
                target.getUsername(),
                null,
                content,
                Instant.now()
        );

        messageRepository.persist(message);

        log.info("sent message to user with id {}", targetUserId);

        return message;
    }

    @Override
    public PanacheQuery<Message> getMessages(UUID userId, UUID targetUserId, int page, int size) {
        log.info("fetching messages of user with id {} and target user with id {} with page {} and size {}", userId, targetUserId, page, size);

        var messages = messageRepository.getMessages(userId, targetUserId, page, size);

        log.info("fetched messages of user with id {} and target user with id {} with page {} and size {}", userId, targetUserId, page, size);

        return messages;
    }

    @Override
    @Transactional
    public Message messageGroup(String content, UUID groupId, UUID senderId, String senderUsername) {
        log.info("sending message to group with id {}", groupId);

        groupUserRepository.findByGroupIdUserId(groupId, senderId)
                .filter(user -> user.getRole().equals(GroupUser.Role.PENDING))
                .orElseThrow(() -> new UnauthorizedException(NOT_MEMBER_OF_GROUP_MESSAGE));

        Message message = new Message(
                UUID.randomUUID(),
                senderId,
                senderUsername,
                null,
                null,
                groupId,
                content,
                Instant.now()
        );

        messageRepository.persist(message);

        log.info("sent message to group with id {}", groupId);

        return message;
    }

    @Override
    public PanacheQuery<Message> getGroupMessages(UUID groupId, UUID userId, int page, int size) {
        log.info("fetching messages of group with id {}, page {} and size {}", groupId, page, size);

        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE);
        }

        groupUserRepository.findByGroupIdUserId(groupId, userId)
                .filter(user -> user.getRole().equals(GroupUser.Role.PENDING))
                .orElseThrow(() -> new UnauthorizedException(NOT_MEMBER_OF_GROUP_MESSAGE));

        var messages = messageRepository.getGroupMessages(groupId, page, size);

        log.info("fetched messages of group with id {}, page {} and size {}", groupId, page, size);

        return messages;
    }
}
