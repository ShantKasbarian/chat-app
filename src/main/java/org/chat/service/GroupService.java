package org.chat.service;

import org.chat.entity.Group;
import org.chat.entity.GroupUser;

import java.util.List;

public interface GroupService {
    Group createGroup(Group group, String[] creators, String userId);
    GroupUser joinGroup(String groupId, String userId);
    String leaveGroup(String groupId, String userId);
    GroupUser acceptJoinGroup(String userId, String groupUserId);
    String rejectJoinGroup(String userId, String groupUserId);
    List<GroupUser> getWaitingUsers(String groupId, String creatorId);
    List<Group> getUserJoinedGroups(String userId);
    List<Group> getGroups(String groupName);
}
