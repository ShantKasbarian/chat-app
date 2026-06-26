package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.GroupUser;
import org.chat.repository.GroupUserRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupUserRepositoryImpl implements GroupUserRepository {
    private static final String GROUP_ID = "groupId";

    private static final String USER_ID = "userId";

    private static final String ROLE = "role";

    private static final String FIND_BY_USER_ID_AND_GROUP_ID = USER_ID + " = ?1 and " + GROUP_ID + " = ?2";

    private static final String FIND_BY_GROUP_ID_AND_ROLE =  GROUP_ID + " = ?1 and " + ROLE + " = ?2";

    @Override
    public Optional<GroupUser> findByGroupIdUserId(UUID groupId, UUID userId) {
        log.debug("fetching groupUser with userId {} and groupId {}", userId, groupId);

        var groupUser = find(FIND_BY_USER_ID_AND_GROUP_ID, userId, groupId)
                .firstResultOptional();

        log.debug("fetched groupUser with userId {} and groupId {}", userId, groupId);

        return groupUser;
    }

    @Override
    public boolean existsByGroupIdUserId(UUID groupId, UUID userId) {
        log.debug("checking if groupUser with userId {} and groupId {}", userId, groupId);

        boolean exists = count(FIND_BY_USER_ID_AND_GROUP_ID, userId, groupId) > 0;

        log.debug("checked if groupUser with userId {} and groupId {}", userId, groupId);

        return exists;
    }

    @Override
    public PanacheQuery<GroupUser> findByRole(UUID groupId, GroupUser.Role role, int page, int size) {
        log.debug("fetching for users who have submitted request to join group");

        var groupUsers = find(FIND_BY_GROUP_ID_AND_ROLE, groupId, role)
                .page(Page.of(page, size));

        log.debug("fetched users who have submitted request to join group");

        return groupUsers;
    }

    @Override
    public PanacheQuery<GroupUser> findByUserId(UUID userId, int page, int size) {
        log.debug("fetching groupUsers with userId {}", userId);

        var groupUsers = find(USER_ID, userId)
                .page(Page.of(page, size));

        log.debug("fetched groupUsers with userId {}", userId);

        return groupUsers;
    }
}
