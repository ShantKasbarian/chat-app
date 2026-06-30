package org.chat.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.GroupMember;

import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends PanacheMongoRepositoryBase<GroupMember, UUID> {
    Optional<GroupMember> findByGroupIdUserId(UUID groupId, UUID userId);
    boolean existsByGroupIdUserId(UUID groupId, UUID userId);
    PanacheQuery<GroupMember> findByRole(UUID groupId, GroupMember.Role role, int page, int size);
    PanacheQuery<GroupMember> findByUserId(UUID userId, int page, int size);
}
