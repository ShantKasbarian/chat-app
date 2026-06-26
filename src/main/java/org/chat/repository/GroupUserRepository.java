package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.GroupUser;

import java.util.Optional;
import java.util.UUID;

public interface GroupUserRepository extends PanacheMongoRepositoryBase<GroupUser, UUID> {
    Optional<GroupUser> findByGroupIdUserId(UUID groupId, UUID userId);
    boolean existsByGroupIdUserId(UUID groupId, UUID userId);
    PanacheQuery<GroupUser> findByRole(UUID groupId, GroupUser.Role role, int page, int size);
    PanacheQuery<GroupUser> findByUserId(UUID userId, int page, int size);
}
