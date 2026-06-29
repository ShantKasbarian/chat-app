package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.model.PageDto;
import org.chat.security.UserPrincipal;

import java.util.UUID;

public interface GroupService {
    Group createGroup(Group group, UserPrincipal userPrincipal);
    GroupUser joinGroup(UUID groupId, UserPrincipal userPrincipal);
    void leaveGroup(UUID groupId, UUID userId);
    GroupUser acceptJoinGroup(UUID userId, UUID groupUserId);
    void rejectJoinGroup(UUID userId, UUID groupUserId);
    PanacheQuery<GroupUser> findUsersWithPendingRole(UUID groupId, UUID userId, int page, int size);
    PageDto<Group> getUserJoinedGroups(UUID userId, int page, int size);
    PanacheQuery<Group> getGroups(String groupName, int page, int size);
}
