package org.chat.model;

import java.util.UUID;
import org.chat.entity.GroupMember;

public record GroupMemberDto(
    UUID id, UUID groupId, UUID userId, String username, GroupMember.Role role) {}
