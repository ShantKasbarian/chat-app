package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.chat.converters.GroupMessageConverter;
import org.chat.converters.MessageConverter;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.Message;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.exceptions.InvalidRoleException;
import org.chat.exceptions.ResourceNotFoundException;
import org.chat.models.GroupMessageDto;
import org.chat.models.MessageDto;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.MessageRepository;
import org.chat.repositories.UserRepository;

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
