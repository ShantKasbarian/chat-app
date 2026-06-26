package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.repository.GroupRepository;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupRepositoryImpl implements GroupRepository {
    private static final String ID = "id";

    private static final String NAME = "name";

    @Override
    public boolean existsById(UUID id) {
        log.debug("checking if group with id {} exists", id);

        boolean exists = count(ID, id) > 0;

        log.debug("checked if group with id {} exists", id);

        return exists;
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("checking if group with name {} exists", name);

        boolean exists = count(NAME, name) > 0;

        log.debug("checked if group with name {} exists", name);

        return exists;
    }

    @Override
    public PanacheQuery<Group> findByName(String groupName, int page, int size) {
        log.debug("fetching groups with name {}", groupName);

        Pattern pattern = Pattern.compile(
                Pattern.quote(groupName),
                Pattern.CASE_INSENSITIVE
        );

        var groups = find(NAME, pattern)
                .page(page, size);

        log.debug("fetched groups with name {}", groupName);

        return groups;
    }

    @Override
    public List<Group> findByIds(List<UUID> ids) {
        log.debug("fetching groups with ids {}", ids);

        var groups = find(ID, ids)
                .list();

        log.debug("fetched groups with ids {}", ids);

        return groups;
    }
}
