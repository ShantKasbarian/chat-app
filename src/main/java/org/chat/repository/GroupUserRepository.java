package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.GroupUser;

import java.util.List;
import java.util.Optional;

public interface GroupUserRepository extends PanacheRepository<GroupUser> {
    Optional<GroupUser> findById(String id);
    GroupUser findByGroupIdUserId(String groupId, String userId);
    List<GroupUser> getWaitingUsers(String groupId);
    List<GroupUser> getUserGroups(String userId);
}
