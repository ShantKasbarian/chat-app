package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.chat.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PanacheRepositoryBase<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> searchByUsername(String username);
}
