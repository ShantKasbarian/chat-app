package org.chat.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private int id;
    private String message;
    @JsonIgnore
    private int senderId;
    private int receiverId;
    private String receiverUsername;
}
