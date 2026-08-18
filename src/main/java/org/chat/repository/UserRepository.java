package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import java.util.Optional;
import java.util.UUID;
import org.chat.entity.User;

public interface UserRepository extends PanacheMongoRepositoryBase<User, UUID> {
  Optional<User> findByUsername(String username);

  PanacheQuery<User> findByUsername(String username, int page, int size);

  boolean existsByUsername(String username);
}
