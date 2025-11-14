package org.chat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.InvalidInfoException;
import org.chat.exception.InvalidRoleException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.chat.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private static final String EMPTY_MESSAGE = "Message is empty";

    private static final String TARGET_USER_NOT_SPECIFIED_MESSAGE = "target user not specified";

    private static final String TARGET_GROUP_NOT_SPECIFIED_MESSAGE = "target group not specified";

    private static final String GROUP_NOT_FOUND_MESSAGE = "group not found";

    private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you are not a member of this group";

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final MessageConverter messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    @Override
    @Transactional
    public Message writeMessage(String content, String recipientId, String currentUserId) {
        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException(EMPTY_MESSAGE);
        }

        if (recipientId == null || recipientId.isEmpty()) {
            throw new InvalidInfoException(TARGET_USER_NOT_SPECIFIED_MESSAGE);
        }

        User recipient = userRepository.findById(recipientId);
        User sender = userRepository.findById(currentUserId);

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setMessage(content);
        message.setTime(LocalDateTime.now());

        messageRepository.persist(message);
        return message;
    }

    @Override
    public List<MessageDto> getMessages(String userId, String recipientId, int page, int size) {
        if (page == 0) {
            page = 1;
        }

        User recipient = userRepository.findById(recipientId);

        return messageRepository.getMessages(userId, recipientId, page, size)
                        .stream()
                        .map(messageConverter::convertToModel)
                        .toList();
    }

    @Override
    @Transactional
    public Message messageGroup(String content, String groupId, String senderId) {
        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException(EMPTY_MESSAGE);
        }

        if (groupId == null || groupId.isEmpty()) {
            throw new InvalidInfoException(TARGET_GROUP_NOT_SPECIFIED_MESSAGE);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), senderId);

        if (groupUser == null || !groupUser.getIsMember()) {
            throw new InvalidRoleException(NOT_MEMBER_OF_GROUP_MESSAGE);
        }

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSender(userRepository.findById(senderId));
        message.setMessage(content);
        message.setGroup(group);
        message.setTime(LocalDateTime.now());

        messageRepository.persist(message);
        return message;
    }

    @Override
    public List<GroupMessageDto> getGroupMessages(String groupId, String userId, int page, int size) {
        if (groupId == null || groupId.isEmpty()) {
            throw new InvalidInfoException(TARGET_GROUP_NOT_SPECIFIED_MESSAGE);
        }

        if (page == 0) {
            page = 1;
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(GROUP_NOT_FOUND_MESSAGE));
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId);

        if (!groupUser.getIsMember()) {
            throw new InvalidRoleException(NOT_MEMBER_OF_GROUP_MESSAGE);
        }

        return messageRepository.getGroupMessages(groupId, page, size)
                .stream()
                .map(groupMessageConverter::convertToModel)
                .toList();
    }
}
