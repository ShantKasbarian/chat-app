package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends PanacheRepository<Group> {
    Optional<Group> findById(String id);
    boolean existsById(String id);
    Optional<Group> findByName(String name);
    List<Group> getGroups(String groupName);
}
