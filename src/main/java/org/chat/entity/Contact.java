package org.chat.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "contacts")
public class Contact {
    @BsonId
    private UUID id;

    private UUID userId;

    private UUID targetUserId;

    private String targetUsernameSnapshot;
}
