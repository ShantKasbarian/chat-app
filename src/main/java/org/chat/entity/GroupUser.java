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
@MongoEntity(collection = "group_users")
public class GroupUser {
    @BsonId
    private UUID id;

    private UUID groupId;

    private UUID userId;

    private Role role;

    enum Role {
        ADMIN,
        MEMBER,
        PENDING
    }
}
