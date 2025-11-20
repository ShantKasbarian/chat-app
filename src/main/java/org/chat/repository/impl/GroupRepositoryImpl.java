package org.chat.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.repository.GroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupRepositoryImpl implements GroupRepository {
    private static final String GROUP_NAME_PARAMETER = "groupName";

    private static final String ID_COLUMN = "id";

    private static final String NAME_PARAMETER = "name";

    private static final String GET_GROUPS_BY_NAME = "FROM Group g WHERE UPPER(g.name) LIKE UPPER(:" + GROUP_NAME_PARAMETER + ")";

    private final EntityManager entityManager;

    @Override
    public boolean existsById(UUID id) {
        log.debug("checking if group with id {} exists", id);

        boolean exists = count(ID_COLUMN, id) > 0;

        log.debug("checked if group with id {} exists", id);

        return exists;
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("checking if group with name {} exists", name);

        boolean exists = count(NAME_PARAMETER, name) > 0;

        log.debug("checked if group with name {} exists", name);

        return exists;
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
