package org.chat.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "messages")
public class Message {
    @BsonId
    private UUID id;

    private UUID senderId;

    private String senderUsernameSnapshot;

    private UUID targetUserId;

    private String targetUsernameSnapshot;

    private UUID groupId;

    private String text;

    private Instant time;
}
