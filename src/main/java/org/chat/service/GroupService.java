package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.model.PageDto;

import java.util.UUID;

public interface GroupService {
    Group createGroup(Group group, UUID userId, String username);
    GroupUser joinGroup(UUID groupId, UUID userId, String username);
    void leaveGroup(UUID groupId, UUID userId);
    GroupUser acceptJoinGroup(UUID userId, UUID groupUserId);
    void rejectJoinGroup(UUID userId, UUID groupUserId);
    PanacheQuery<GroupUser> findUsersWithPendingRole(UUID groupId, UUID creatorId, int page, int size);
    PageDto<Group> getUserJoinedGroups(UUID userId, int page, int size);
    PanacheQuery<Group> getGroups(String groupName, int page, int size);
}
