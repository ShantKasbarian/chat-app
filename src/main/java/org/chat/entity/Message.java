package org.chat.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@MongoEntity(collection = "messages")
public class Message {
    @BsonId
    private UUID id;

    private UUID senderId;

    private UUID targetUserId;

    private UUID groupId;

    private String text;

    private LocalDateTime time;
}
