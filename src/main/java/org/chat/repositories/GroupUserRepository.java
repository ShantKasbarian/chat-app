package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.chat.entities.GroupUser;
import org.chat.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class GroupUserRepository implements PanacheRepository<GroupUser> {
    private final EntityManager entityManager;

    public GroupUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public GroupUser findByGroupIdUserId(int groupId, int userId) {
        GroupUser groupUser = null;
        try {
             groupUser =
                    entityManager
                            .createQuery(
                        "from GroupUser gu where gu.group.id = :groupId and gu.user.id = :userId",
                        GroupUser.class
                )
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult();

        }
        catch (Exception e) {
            throw new ResourceNotFoundException("user is not part of this group");
        }

        return groupUser;
    }

    public List<GroupUser> getWaitingUsers(int groupId) {
        return entityManager.createQuery(
                "from GroupUser gu where gu.group.id = :groupId and gu.isMember = false",
                GroupUser.class
        )
        .setParameter("groupId", groupId)
        .getResultList();
    }

    public List<GroupUser> getUserGroups(int userId) {
        return entityManager
                .createQuery(
                "from GroupUser gu where gu.user.id = :userId and gu.isMember = true",
                        GroupUser.class
        ).setParameter("userId", userId)
        .getResultList();
    }
}
