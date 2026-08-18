package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import java.util.UUID;
import org.chat.entity.GroupMember;
import org.chat.security.UserPrincipal;

public interface GroupMemberService {
  GroupMember joinGroup(UUID groupId, UserPrincipal userPrincipal);

  void leaveGroup(UUID groupId, UUID userId);

  GroupMember acceptJoinRequest(UUID userId, UUID groupMemberId);

  void rejectJoinRequest(UUID userId, UUID groupMemberId);

  PanacheQuery<GroupMember> findUsersByRole(
      UUID groupId, UUID userId, GroupMember.Role role, int page, int size);
}
