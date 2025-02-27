package org.chat.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private int id;
    private String message;
    private int senderId;
    private int receiverId;
    private String receiverUsername;
    private String groupName;
}
