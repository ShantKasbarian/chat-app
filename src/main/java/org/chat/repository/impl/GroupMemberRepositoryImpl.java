package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.GroupMember;
import org.chat.repository.GroupMemberRepository;

@Slf4j
@ApplicationScoped
public class GroupMemberRepositoryImpl implements GroupMemberRepository {
  private static final String GROUP_ID = "groupId";

  private static final String USER_ID = "userId";

  private static final String ROLE = "role";

  private static final String FIND_BY_USER_ID_AND_GROUP_ID =
      USER_ID + " = ?1 and " + GROUP_ID + " = ?2";

  private static final String FIND_BY_GROUP_ID_AND_ROLE = GROUP_ID + " = ?1 and " + ROLE + " = ?2";

  @Override
  public Optional<GroupMember> findByGroupIdUserId(UUID groupId, UUID userId) {
    log.debug("fetching groupMember with userId {} and groupId {}", userId, groupId);

    var groupMember = find(FIND_BY_USER_ID_AND_GROUP_ID, userId, groupId).firstResultOptional();

    log.debug("fetched groupMember with userId {} and groupId {}", userId, groupId);

    return groupMember;
  }

  @Override
  public boolean existsByGroupIdUserId(UUID groupId, UUID userId) {
    log.debug("checking if groupMember with userId {} and groupId {}", userId, groupId);

    boolean exists = count(FIND_BY_USER_ID_AND_GROUP_ID, userId, groupId) > 0;

    log.debug("checked if groupMember with userId {} and groupId {}", userId, groupId);

    return exists;
  }

  @Override
  public PanacheQuery<GroupMember> findByRole(
      UUID groupId, GroupMember.Role role, int page, int size) {
    log.debug("fetching for users with groupId {}, role {}", groupId, role);

    var groupMembers = find(FIND_BY_GROUP_ID_AND_ROLE, groupId, role).page(Page.of(page, size));

    log.debug("fetched users with groupId {}, role {}", groupId, role);

    return groupMembers;
  }

  @Override
  public PanacheQuery<GroupMember> findByUserId(UUID userId, int page, int size) {
    log.debug("fetching groupMembers with userId {}", userId);

    var groupMembers = find(USER_ID, userId).page(Page.of(page, size));

    log.debug("fetched groupMembers with userId {}", userId);

    return groupMembers;
  }
}
