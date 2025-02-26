package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.chat.entities.GroupUser;

import java.util.List;

@ApplicationScoped
public class GroupUserRepository implements PanacheRepository<GroupUser> {
    private final EntityManager entityManager;

    public GroupUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public GroupUser findByGroupIdUserId(int groupId, int userId) {
        return entityManager
            .createQuery(
                    "from GroupUser gu where gu.group.id = :group and gu.userId = :userId",
                    GroupUser.class
            )
            .setParameter("group", groupId)
            .setParameter("userId", userId)
            .getSingleResult();
    }

    public List<String> getWaitingUsers(int groupId) {
        Query query = entityManager.createNativeQuery(
                "select u.username from users u " +
                "left join group_users gu on gu.user_id = u.id and gu.is_member = false " +
                "where group_id = ?"
        ).setParameter(1, groupId);

        return query.getResultList();
    }

    public List<String> getUserGroups(int userId) {
        Query query = entityManager.createNativeQuery(
                "select g.name from groups g " +
                "left join group_users gu on gu.group_id = g.id and gu.is_member = true " +
                "where gu.user_id = ?"
        ).setParameter(1, userId);

        return query.getResultList();
    }
}
