package org.chat.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.chat.entities.Message;
import org.chat.entities.User;
import org.chat.repositories.MessageRepository;
import org.chat.repositories.UserRepository;

import java.util.List;

@ApplicationScoped
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public String writeMessage(Message message, String recepientUsername) {
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message is empty");
        }

        User recipient = userRepository.findByUsername(recepientUsername);

        if (
                recepientUsername == null ||
                recipient == null
        ) {
            throw new IllegalArgumentException("Recepient not found");
        }

        message.setRecepient(recipient.getId());
        messageRepository.persist(message);

        return "message has been sent";
    }

    public List<Message> getMessages(int userId, String recepientUsername) {
        return messageRepository.getMessages(userId, recepientUsername);
    }
}
