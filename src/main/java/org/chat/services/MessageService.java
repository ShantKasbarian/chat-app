package org.chat.services;

import io.quarkus.runtime.Startup;
import jakarta.transaction.Transactional;
import org.chat.converters.MessageRepresentationConverter;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.Message;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.exceptions.InvalidRoleException;
import org.chat.models.MessageRepresentationDto;
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
    private final MessageRepresentationConverter messageRepresentationConverter;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupUserRepository groupUserRepository,
            MessageRepresentationConverter messageRepresentationConverter
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.messageRepresentationConverter = messageRepresentationConverter;
    }

    @Transactional
    public String writeMessage(Message message, String recipientUsername, Long currentUserId) {
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (recipientUsername == null) {
            throw new InvalidInfoException("username not specified");
        }

        User recipient = userRepository.findByUsername(recipientUsername);
        User sender = userRepository.findById(currentUserId);

        message.setRecipient(recipient);
        message.setSender(sender);
        message.setTime(LocalDateTime.now());
        messageRepository.save(message);

        return "message has been sent";
    }

    public List<MessageRepresentationDto> getMessages(int userId, String recipientUsername) {
        User recipient = userRepository.findByUsername(recipientUsername);

        List<MessageRepresentationDto> messages =
                messageRepository.getMessages(userId, recipient.getId())
                        .stream()
                        .map(messageRepresentationConverter::convertToModel)
                        .sorted(Comparator.comparing(MessageRepresentationDto::time))
                        .toList();

        return messages.reversed();
    }

    public String messageGroup(Message message, String groupName, Long senderId) {
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (groupName == null) {
            throw new InvalidInfoException("group name not specified");
        }

        Group group = groupRepository.findByName(groupName);

        message.setSender(userRepository.findById(senderId));
        message.setGroup(group);
        message.setTime(LocalDateTime.now());

        messageRepository.saveGroupMessage(message);
        return "message has been sent";
    }

    public List<MessageRepresentationDto> getGroupMessages(String groupName, int userId) {
        if (groupName == null) {
            throw new InvalidInfoException("group name not specified");
        }

        Group group = groupRepository.findByName(groupName);
        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);

        if (!groupUser.getIsMember()) {
            throw new InvalidRoleException("you're not in group");
        }

        return messageRepository.getGroupMessages(group.getId())
                .stream()
                .map(messageRepresentationConverter::convertToModel)
                .sorted(Comparator.comparing(MessageRepresentationDto::time))
                .toList()
                .reversed();
    }
}
