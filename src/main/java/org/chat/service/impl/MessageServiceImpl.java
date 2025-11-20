package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupMessageConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.InvalidInfoException;
import org.chat.exception.InvalidRoleException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.chat.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private static final String USER_NOT_FOUND = "user not found";

    private static final String EMPTY_MESSAGE = "Message is empty";

    private static final String TARGET_USER_NOT_SPECIFIED_MESSAGE = "target user not specified";

    private static final String TARGET_GROUP_NOT_SPECIFIED_MESSAGE = "target group not specified";

    private static final String GROUP_NOT_FOUND_MESSAGE = "group not found";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you are not a member of this group";

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final GroupMessageConverter groupMessageConverter;

    @Override
    @Transactional
    public Message sendMessage(String content, UUID recipientId, UUID currentUserId) {
        log.info("sending message to user with id {}", recipientId);

        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException(EMPTY_MESSAGE);
        }

        if (recipientId == null) {
            throw new InvalidInfoException(TARGET_USER_NOT_SPECIFIED_MESSAGE);
        }

        User recipient = userRepository.findById(recipientId);

        User sender = userRepository.findById(currentUserId);

        Message message = new Message();
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setText(content);
        message.setTime(LocalDateTime.now());

        messageRepository.persist(message);

        log.info("sent message to user with id {}", recipientId);

        return message;
    }

    @Override
    public List<Message> getMessages(UUID userId, UUID recipientId, int page, int size) {
        log.info("fetching messages of user with id {} and recipient with id {} with page {} and size {}", userId, recipientId, page, size);

        if (page == 0) {
            page = 1;
        }

        var messages = messageRepository.getMessages(userId, recipientId, page, size);

        log.info("fetching messages of user with id {} and recipient with id {} with page {} and size {}", userId, recipientId, page, size);

        return messages;
    }

    @Override
    @Transactional
    public Message messageGroup(String content, UUID groupId, UUID senderId) {
        log.info("sending message to group with id {}", groupId);

        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException(EMPTY_MESSAGE);
        }

        if (groupId == null) {
            throw new InvalidInfoException(TARGET_GROUP_NOT_SPECIFIED_MESSAGE);
        }

        Group group = groupRepository.findById(groupId);

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, senderId);

        if (groupUser == null || !groupUser.getIsMember()) {
            throw new InvalidRoleException(NOT_MEMBER_OF_GROUP_MESSAGE);
        }

        User currentUser = userRepository.findById(senderId);

        Message message = new Message();
        message.setSender(currentUser);
        message.setText(content);
        message.setGroup(group);
        message.setTime(LocalDateTime.now());

        messageRepository.persist(message);

        log.info("sent message to group with id {}", groupId);

        return message;
    }

    @Override
    public List<Message> getGroupMessages(UUID groupId, UUID userId, int page, int size) {
        log.info("fetching messages of group with id {}, page {} and size {}", groupId, page, size);

        if (groupId == null) {
            throw new InvalidInfoException(TARGET_GROUP_NOT_SPECIFIED_MESSAGE);
        }

        if (page == 0) {
            page = 1;
        }

        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE);
        }

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId);

        if (!groupUser.getIsMember()) {
            throw new InvalidRoleException(NOT_MEMBER_OF_GROUP_MESSAGE);
        }

        var messages = messageRepository.getGroupMessages(groupId, page, size);

        log.info("fetched messages of group with id {}, page {} and size {}", groupId, page, size);

        return messages;
    }
}
