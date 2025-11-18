package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.GroupUser;
import org.chat.repository.GroupUserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupUserRepositoryImpl implements GroupUserRepository {
    private static final String ID_COLUMN = "id";

    private static final String GROUP_ID_PARAMETER = "groupId";

    private static final String USER_ID_PARAMETER = "userId";

    private static final String FIND_BY_GROUP_ID_USER_ID = "FROM GroupUser gu WHERE gu.group.id = :" + GROUP_ID_PARAMETER + " AND gu.user.id = :" + USER_ID_PARAMETER;

    private static final String EXISTS_BY_GROUP_ID_USER_ID = "SELECT COUNT(gu) > 0 FROM GroupUser gu WHERE gu.group.id = :" + GROUP_ID_PARAMETER + " AND gu.user.id = :" + USER_ID_PARAMETER;

    private static final String GET_USERS_WITH_SUBMITTED_REQUEST = "FROM GroupUser gu WHERE gu.group.id = :" + GROUP_ID_PARAMETER + " AND gu.isMember = false";

    private static final String GET_USER_GROUPS = "FROM GroupUser gu WHERE gu.user.id = :" + USER_ID_PARAMETER + " AND gu.isMember = true";

    private final EntityManager entityManager;

    @Override
    public Optional<GroupUser> findById(String id) {
        log.info("fetching groupUser with id {}", id);

        Optional<GroupUser> groupUser = find(ID_COLUMN, id).firstResultOptional();

        log.info("fetched groupUser with id {}", id);

        return groupUser;
    }

    @Override
    public GroupUser findByGroupIdUserId(String groupId, String userId) {
        log.debug("fetching groupUser with userId {} and groupId {}", userId, groupId);

        GroupUser groupUser = entityManager.createQuery(FIND_BY_GROUP_ID_USER_ID, GroupUser.class)
                .setParameter(GROUP_ID_PARAMETER, groupId)
                .setParameter(USER_ID_PARAMETER, userId)
                .getSingleResult();

        log.debug("fetched groupUser with userId {} and groupId {}", userId, groupId);

        return groupUser;
    }

    @Override
    public boolean existsByGroupIdUserId(String groupId, String userId) {
        try {
            log.debug("checking if groupUser with userId {} and groupId {}", userId, groupId);

            entityManager.createQuery(EXISTS_BY_GROUP_ID_USER_ID, Boolean.class)
                    .setParameter(GROUP_ID_PARAMETER, groupId)
                    .setParameter(USER_ID_PARAMETER, userId)
                    .getSingleResult();

            log.debug("checked if groupUser with userId {} and groupId {}", userId, groupId);
        }
        catch (NoResultException e) {
            return false;
        }

        return true;
    }

    @Override
    public List<GroupUser> getWaitingUsers(String groupId) {
        log.debug("fetching for users who have submitted request to join group");

        var groupUsers = entityManager.createQuery(GET_USERS_WITH_SUBMITTED_REQUEST, GroupUser.class)
                .setParameter(GROUP_ID_PARAMETER, groupId)
                .getResultList();

        log.debug("fetched users who have submitted request to join group");

        return groupUsers;
    }

    @Override
    public List<GroupUser> getUserGroups(String userId) {
        log.debug("fetching groupUsers with userId {}", userId);

        var groupUsers = entityManager.createQuery(GET_USER_GROUPS, GroupUser.class)
                .setParameter(USER_ID_PARAMETER, userId)
                .getResultList();

        log.debug("fetched groupUsers with userId {}", userId);

        return groupUsers;
    }
}
