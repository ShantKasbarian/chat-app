package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PanacheMongoRepositoryBase<User, UUID> {
    Optional<User> findByUsername(String username);
    PanacheQuery<User> findByUsername(String username, int page, int size);
    boolean existsById(UUID id);
    boolean existsByUsername(String username);
}
