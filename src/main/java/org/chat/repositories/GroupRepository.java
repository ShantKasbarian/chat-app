package org.chat.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entities.Group;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupRepository implements PanacheRepository<Group> {
    private static final String GROUP_NAME_PARAMETER = "groupName";

    private static final String GET_GROUPS_BY_NAME = "from Group g where upper(g.name) like upper(:" + GROUP_NAME_PARAMETER + ")";

    private final EntityManager entityManager;

    public Optional<Group> findById(String id) {
        log.debug("fetching group with id {}", id);

        Optional<Group> group = Optional.of(entityManager.find(Group.class, id));

        log.debug("fetched group with id {}", id);

        return group;
    }

    public List<Group> getGroups(String groupName) {
        log.debug("fetching groups with name {}", groupName);

        var groups = entityManager
                .createQuery(GET_GROUPS_BY_NAME, Group.class)
                .setParameter(GROUP_NAME_PARAMETER, "%" + groupName + "%")
                .getResultList();

        log.debug("fetched groups with name {}", groupName);

        return groups;
    }
}
