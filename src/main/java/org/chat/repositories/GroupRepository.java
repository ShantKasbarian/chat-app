package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.chat.entities.Group;

import java.util.List;

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

    public List<String> getGroups(String groupName) {
        return entityManager
                .createQuery(
                        "select g.name from Group g where upper(g.name) like upper(:groupName)",
                        String.class
                )
                .setParameter("groupName", "%" + groupName + "%")
                .getResultList();

    }
}
