package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import org.chat.entity.Group;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends PanacheMongoRepositoryBase<Group, UUID> {
    boolean existsById(UUID id);
    boolean existsByName(String name);
    List<Group> getGroups(String groupName);
    List<Group> getUserGroups(UUID userId);
}
