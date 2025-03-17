package org.chat.services;

import io.quarkus.runtime.Startup;
import jakarta.transaction.Transactional;
import org.chat.converters.GroupMessageConverter;
import org.chat.converters.MessageConverter;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.Message;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.exceptions.InvalidRoleException;
import org.chat.models.GroupMessageDto;
import org.chat.models.MessageDto;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.MessageRepository;
import org.chat.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Startup
public class MessageService {
    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    private final MessageConverter messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupUserRepository groupUserRepository,
            MessageConverter messageConverter,
            GroupMessageConverter groupMessageConverter
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.messageConverter = messageConverter;
        this.groupMessageConverter = groupMessageConverter;
    }

    @Transactional
    public Message writeMessage(String content, String recipientUsername, Long currentUserId) {
        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (recipientUsername == null) {
            throw new InvalidInfoException("username not specified");
        }

        User recipient = userRepository.findByUsername(recipientUsername);
        User sender = userRepository.findById(currentUserId);

        Message message = new Message();
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setMessage(content);
        message.setTime(LocalDateTime.now());

        message.persist();
        return message;
    }

    public List<MessageDto> getMessages(Long userId, String recipientUsername) {
        User recipient = userRepository.findByUsername(recipientUsername);

        return messageRepository.getMessages(userId, recipient.id)
                        .stream()
                        .map(messageConverter::convertToModel)
                        .sorted(Comparator.comparing(MessageDto::time))
                        .toList()
                        .reversed();
    }

    @Transactional
    public Message messageGroup(String content, String groupName, Long senderId) {
        if (content == null || content.isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (groupName == null) {
            throw new InvalidInfoException("group name not specified");
        }

        Group group = groupRepository.findByName(groupName);

        Message message = new Message();
        message.setSender(userRepository.findById(senderId));
        message.setMessage(content);
        message.setGroup(group);
        message.setTime(LocalDateTime.now());

        message.persist();
        return message;
    }

    public List<GroupMessageDto> getGroupMessages(String groupName, Long userId) {
        if (groupName == null) {
            throw new InvalidInfoException("group name not specified");
        }

        Group group = groupRepository.findByName(groupName);
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.id, userId);

        if (!groupUser.getIsMember()) {
            throw new InvalidRoleException("you're not in group");
        }

        return messageRepository.getGroupMessages(group.id)
                .stream()
                .map(groupMessageConverter::convertToModel)
                .sorted(Comparator.comparing(GroupMessageDto::time))
                .toList()
                .reversed();
    }
}
