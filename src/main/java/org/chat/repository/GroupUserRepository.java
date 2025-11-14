package org.chat.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.GroupUser;
import org.chat.exception.ResourceNotFoundException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupUserRepository implements PanacheRepository<GroupUser> {
    private static final String GROUP_ID_PARAMETER = "groupId";

    private static final String USER_ID_PARAMETER = "userId";

    private static final String GET_GROUP_USER = "from GroupUser gu where gu.group.id = " + GROUP_ID_PARAMETER + " and gu.user.id = :" + USER_ID_PARAMETER;

    private static final String GET_USERS_WITH_SUBMITTED_REQUEST = "from GroupUser gu where gu.group.id = :" + GROUP_ID_PARAMETER + " and gu.isMember = false";

    private static final String GET_USER_GROUPS = "from GroupUser gu where gu.user.id = :" + USER_ID_PARAMETER + " and gu.isMember = true";

    private final EntityManager entityManager;

    public GroupUser findByGroupIdUserId(String groupId, String userId) {
        log.debug("fetching groupUser with userId {} and groupId {}", userId, groupId);

        GroupUser groupUser = null;

        try {
             groupUser = entityManager.createQuery(GET_GROUP_USER, GroupUser.class)
                    .setParameter(GROUP_ID_PARAMETER, groupId)
                    .setParameter(USER_ID_PARAMETER, userId)
                    .getSingleResult();
        }
        catch (NoResultException e) {
            throw new ResourceNotFoundException("user is not part of this group");
        }

        log.debug("fetched groupUser with userId {} and groupId {}", userId, groupId);

        return groupUser;
    }

    public List<GroupUser> getWaitingUsers(String groupId) {
        log.debug("fetching for users who have submitted request to join group");

        var groupUsers = entityManager.createQuery(GET_USERS_WITH_SUBMITTED_REQUEST, GroupUser.class)
                .setParameter(GROUP_ID_PARAMETER, groupId)
                .getResultList();

        log.debug("fetched users who have submitted request to join group");

        return groupUsers;
    }

    public List<GroupUser> getUserGroups(String userId) {
        log.debug("fetching groupUsers with userId {}", userId);

        var groupUsers = entityManager.createQuery(GET_USER_GROUPS, GroupUser.class)
                .setParameter(USER_ID_PARAMETER, userId)
                .getResultList();

        log.debug("fetched groupUsers with userId {}", userId);

        return groupUsers;
    }
}
