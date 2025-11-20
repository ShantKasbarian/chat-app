package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.chat.entity.GroupUser;

import java.util.List;
import java.util.UUID;

public interface GroupUserRepository extends PanacheRepositoryBase<GroupUser, UUID> {
    GroupUser findByGroupIdUserId(UUID groupId, UUID userId);
    boolean existsByGroupIdUserId(UUID groupId, UUID userId);
    List<GroupUser> getWaitingUsers(UUID groupId);
    List<GroupUser> getUserGroups(UUID userId);
}
