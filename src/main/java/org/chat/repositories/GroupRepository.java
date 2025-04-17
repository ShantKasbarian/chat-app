package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.chat.entities.Group;
import org.chat.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

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
        catch (NoResultException e) {
            throw new ResourceNotFoundException("group not found");
        }
    }

    public Optional<Group> findById(String id) {
        return Optional.of(entityManager.find(Group.class, id));
    }

    public List<Group> getGroups(String groupName) {
        return entityManager
                .createQuery(
                        "from Group g where upper(g.name) like upper(:groupName)",
                        Group.class
                )
                .setParameter("groupName", "%" + groupName + "%")
                .getResultList();

    }
}
