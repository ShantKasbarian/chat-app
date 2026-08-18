package org.chat.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "messages")
public class Message {
  @BsonId private UUID id;

  private UUID senderId;

  private String senderUsernameSnapshot;

  private UUID targetUserId;

  private String targetUsernameSnapshot;

  private UUID groupId;

  private String groupNameSnapshot;

  private String text;

  private Type type;

  private Instant time;

  public enum Type {
    USER,
    GROUP
  }
}
