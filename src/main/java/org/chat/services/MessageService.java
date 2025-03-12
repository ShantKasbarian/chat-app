package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Group;
import org.chat.entities.GroupUser;
import org.chat.entities.Message;
import org.chat.entities.User;
import org.chat.exceptions.InvalidInfoException;
import org.chat.exceptions.InvalidRoleException;
import org.chat.exceptions.NotFoundException;
import org.chat.models.GroupMessageDto;
import org.chat.repositories.GroupRepository;
import org.chat.repositories.GroupUserRepository;
import org.chat.repositories.MessageRepository;
import org.chat.repositories.UserRepository;

import java.util.List;

@ApplicationScoped
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;

    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupUserRepository groupUserRepository
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupUserRepository = groupUserRepository;
    }

    public String writeMessage(Message message, String recipientUsername) {
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (recipientUsername == null) {
            throw new InvalidInfoException("username not specified");
        }

        User recipient = userRepository.findByUsername(recipientUsername);

        if (recipient == null) {
            throw new NotFoundException("Recipient not found");
        }

        message.setRecipient(recipient.getId());
        messageRepository.save(message);

        return "message has been sent";
    }

    public List<GroupMessageDto> getMessages(int userId, String recipientUsername) {
        User user = userRepository.findByUsername(recipientUsername);

        if (user == null) {
            throw new NotFoundException("user not found");
        }

        return messageRepository.getMessages(
                userId,
                user.getId()
        );
    }

    public String messageGroup(Message message, String groupName) {
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            throw new InvalidInfoException("Message is empty");
        }

        if (groupName == null) {
            throw new InvalidInfoException("group name not specified");
        }

        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new NotFoundException("Group not found");
        }

        message.setGroup(group);
        messageRepository.save(message.getMessage(), group.getId(), message.getSenderId());

        return "message has been sent";
    }

    public List<GroupMessageDto> getGroupMessages(String groupName, int userId) {
        if (groupName == null) {
            throw new InvalidInfoException("group name not specified");
        }

        Group group = groupRepository.findByName(groupName);

        if (group == null) {
            throw new NotFoundException("Group not found");
        }

        GroupUser groupUser = groupUserRepository.findByGroupIdUserId(group.getId(), userId);

        if (groupUser == null || !groupUser.getIsMember()) {
            throw new InvalidRoleException("you're not in group");
        }

        return messageRepository.getMessages(group.getId());
    }
}
