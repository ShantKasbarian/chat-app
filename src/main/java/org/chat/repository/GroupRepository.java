package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.chat.entity.Group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends PanacheRepositoryBase<Group, UUID> {
    boolean existsById(UUID id);
    boolean existsByName(String name);
    List<Group> getGroups(String groupName);
}
