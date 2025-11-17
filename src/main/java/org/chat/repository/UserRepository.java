package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.User;

import java.util.List;

public interface UserRepository extends PanacheRepository<User> {
    User findById(String id);
    boolean existsById(String id);
    User findByUsername(String username);
    List<User> searchByUsername(String username);
}
