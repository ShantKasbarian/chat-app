package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.chat.entity.GroupUser;

import java.util.List;

public interface GroupUserRepository extends PanacheRepository<GroupUser> {
    GroupUser findByGroupIdUserId(String groupId, String userId);
    List<GroupUser> getWaitingUsers(String groupId);
    List<GroupUser> getUserGroups(String userId);
}
