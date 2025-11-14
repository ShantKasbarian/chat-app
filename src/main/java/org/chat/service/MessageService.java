package org.chat.service;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final MessageConverter messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    @Transactional
    public Message writeMessage(String content, String recipientId, String currentUserId) {
        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (recipientId == null || recipientId.isEmpty()) {
            throw new InvalidInfoException("username not specified");
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

    @Transactional
    public Message messageGroup(String content, String groupId, String senderId) {
        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (groupId == null || groupId.isEmpty()) {
            throw new InvalidInfoException("group id not specified");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), senderId);

        if (groupUser == null || !groupUser.getIsMember()) {
            throw new InvalidRoleException("you are not a member of this group");
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

    public List<GroupMessageDto> getGroupMessages(String groupId, String userId, int page, int size) {
        if (groupId == null || groupId.isEmpty()) {
            throw new InvalidInfoException("group name not specified");
        }

        if (page == 0) {
            page = 1;
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(groupId, userId);

        if (!groupUser.getIsMember()) {
            throw new InvalidRoleException("you're not in group");
        }

        return messageRepository.getGroupMessages(groupId, page, size)
                .stream()
                .map(groupMessageConverter::convertToModel)
                .toList();
    }
}
