package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.GroupUser;
import org.chat.security.UserPrincipal;

import java.util.UUID;

public interface GroupUserService {
    GroupUser joinGroup(UUID groupId, UserPrincipal userPrincipal);
    void leaveGroup(UUID groupId, UUID userId);
    GroupUser acceptJoinGroup(UUID userId, UUID groupUserId);
    void rejectJoinGroup(UUID userId, UUID groupUserId);
    PanacheQuery<GroupUser> findUsersByRole(UUID groupId, UUID userId, GroupUser.Role role, int page, int size);
}
