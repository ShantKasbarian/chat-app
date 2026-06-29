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
import org.chat.security.UserPrincipal;
import org.chat.service.MessageService;

import java.time.Instant;
import java.util.UUID;

import static org.chat.service.impl.GroupServiceImpl.REQUEST_NOT_AUTHORIZED;

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
    public Message sendMessage(UserPrincipal userPrincipal, String content, UUID targetUserId) {
        log.info("sending message to user with id {}", targetUserId);

        User target = userRepository.findByIdOptional(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        Message message = new Message(
                UUID.randomUUID(),
                userPrincipal.id(),
                userPrincipal.username(),
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

        var messages = messageRepository.findByUserIdTargetUserId(userId, targetUserId, page, size);

        log.info("fetched messages of user with id {} and target user with id {} with page {} and size {}", userId, targetUserId, page, size);

        return messages;
    }

    @Override
    @Transactional
    public Message messageGroup(UserPrincipal userPrincipal, String content, UUID groupId) {
        log.info("sending message to group with id {}", groupId);

        UUID userId = userPrincipal.id();

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (groupUser.getRole().equals(GroupUser.Role.PENDING)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        Message message = new Message(
                UUID.randomUUID(),
                userId,
                userPrincipal.username(),
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

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

        if (groupUser.getRole().equals(GroupUser.Role.PENDING)) {
            throw new UnauthorizedException(REQUEST_NOT_AUTHORIZED);
        }

        var messages = messageRepository.findByGroupId(groupId, page, size);

        log.info("fetched messages of group with id {}, page {} and size {}", groupId, page, size);

        return messages;
    }
}
