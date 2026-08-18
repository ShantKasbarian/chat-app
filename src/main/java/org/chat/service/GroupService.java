package org.chat.service;

import io.quarkus.mongodb.panache.PanacheQuery;
import java.util.UUID;
import org.chat.entity.Group;
import org.chat.model.PageDto;
import org.chat.security.UserPrincipal;

public interface GroupService {
  Group createGroup(Group group, UserPrincipal userPrincipal);

  PageDto<Group> getUserJoinedGroups(UUID userId, int page, int size);

  PanacheQuery<Group> getGroups(String name, int page, int size);
}
