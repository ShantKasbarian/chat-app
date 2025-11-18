package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PanacheRepository<User> {
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> searchByUsername(String username);
}
