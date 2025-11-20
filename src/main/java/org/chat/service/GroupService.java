package org.chat.service;

import org.chat.entity.Group;
import org.chat.entity.GroupUser;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    Group createGroup(Group group, UUID[] creators, UUID userId);
    GroupUser joinGroup(UUID groupId, UUID userId);
    String leaveGroup(UUID groupId, UUID userId);
    GroupUser acceptJoinGroup(UUID userId, UUID groupUserId);
    String rejectJoinGroup(UUID userId, UUID groupUserId);
    List<GroupUser> getWaitingUsers(UUID groupId, UUID creatorId);
    List<Group> getUserJoinedGroups(UUID userId);
    List<Group> getGroups(String groupName);
}
