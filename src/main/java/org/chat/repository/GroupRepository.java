package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends PanacheMongoRepositoryBase<Group, UUID> {
    boolean existsByName(String name);
    PanacheQuery<Group> findByName(String name, int page, int size);
    List<Group> findByIds(List<UUID> ids);
}
