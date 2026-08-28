package org.chat.repository.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.repository.GroupRepository;

@Slf4j
@ApplicationScoped
public class GroupRepositoryImpl implements GroupRepository {
  private static final String FIND_BY_IDS_QUERY = "_id in ?1";

  private static final String NAME = "name";

  @Override
  public boolean existsByName(String name) {
    log.debug("checking if group with name {} exists", name);

    boolean exists = count(NAME, name) > 0;

    log.debug("checked if group with name {} exists", name);

    return exists;
  }

  @Override
  public PanacheQuery<Group> findByName(String name, int page, int size) {
    log.debug("fetching groups with name {}", name);

    Pattern pattern = Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE);
    var query = find(NAME, pattern).page(Page.of(page, size));

    log.debug("fetched groups with name {}", name);

    return query;
  }

  @Override
  public List<Group> findByIds(List<UUID> ids) {
    log.debug("fetching groups with ids {}", ids);

    var groups = find(FIND_BY_IDS_QUERY, ids).list();

    log.debug("fetched groups with ids {}", ids);

    return groups;
  }
}
