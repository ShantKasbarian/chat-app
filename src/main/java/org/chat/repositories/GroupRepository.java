package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.chat.entities.Group;

@ApplicationScoped
public class GroupRepository implements PanacheRepository<Group> {
    private final EntityManager entityManager;
    
    public GroupRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Group findByName(String name) {
        try {
            return entityManager.createQuery(
                    "from Group g where g.name = :name",
                    Group.class
            )
            .setParameter("name", name)
            .getSingleResult();
        }
        catch (Exception e) {
            return null;
        }
    }
}
