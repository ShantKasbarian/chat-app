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
@MongoEntity(collection = "users")
public class User {
  @BsonId private UUID id;

  private String username;

  private String password;
}
