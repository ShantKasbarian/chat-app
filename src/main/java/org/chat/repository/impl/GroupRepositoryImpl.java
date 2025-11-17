package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupRepositoryImpl implements GroupRepository {
    private static final String GROUP_NAME_PARAMETER = "groupName";

    private static final String ID_PARAMETER = "id";

    private static final String GET_GROUPS_BY_NAME = "FROM Group g WHERE UPPER(g.name) LIKE UPPER(:" + GROUP_NAME_PARAMETER + ")";

    private static final String EXISTS_BY_ID_QUERY = "SELECT COUNT(g) = 1 FROM GROUP g WHERE g.id = :" + ID_PARAMETER;

    private static final String NAME_COLUMN = "name";

    private final EntityManager entityManager;

    @Override
    public Optional<Group> findById(String id) {
        log.debug("fetching group with id {}", id);

        Optional<Group> group = Optional.of(entityManager.find(Group.class, id));

        log.debug("fetched group with id {}", id);

        return group;
    }

    @Override
    public boolean existsById(String id) {
        log.debug("checking if group with id {} exists", id);

        boolean exists = entityManager.createQuery(EXISTS_BY_ID_QUERY, Boolean.class)
                .setParameter(ID_PARAMETER, id)
                .getSingleResult();

        log.debug("checked if group with id {} exists", id);

        return exists;
    }

    @Override
    public Optional<Group> findByName(String name) {
        log.debug("fetching group with name {}", name);

        Optional<Group> group = Optional.of(find(NAME_COLUMN, name).firstResult());

        log.debug("fetched group with name {}", name);

        return group;
    }

    @Override
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
