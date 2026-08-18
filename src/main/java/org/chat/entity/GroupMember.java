package org.chat.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "group_members")
public class GroupMember {
  @BsonId private UUID id;

  private UUID groupId;

  private UUID userId;

  private String usernameSnapshot;

  private Role role;

  public enum Role {
    ADMIN,
    MEMBER,
    PENDING
  }
}
