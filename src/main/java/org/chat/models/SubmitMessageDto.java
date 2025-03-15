package org.chat.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubmitMessageDto {
    private int id;
    private String message;
    private int senderId;
    private int receiverId;
    private String receiverUsername;
    private String groupName;
    private LocalDateTime time;

    @Override
    public String toString() {
        return "SubmitMessageDto{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", receiverUsername='" + receiverUsername + '\'' +
                ", groupName='" + groupName + '\'' +
                ", time=" + time +
                '}';
    }
}
