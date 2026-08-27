package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.exception.*;
import org.chat.model.GroupDto;
import org.chat.model.PageDto;
import org.chat.repository.GroupMemberRepository;
import org.chat.repository.GroupRepository;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupService;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupServiceImpl implements GroupService {
  static final String REQUEST_NOT_AUTHORIZED =
      "You do not have the necessary permissions to perform this request";

  private static final String GROUP_ALREADY_EXISTS_MESSAGE = "Group already exists";

  private final GroupRepository groupRepository;

  private final GroupMemberRepository groupMemberRepository;

  @Override
  public Group createGroup(GroupDto groupDto, UserPrincipal userPrincipal) {
    String groupName = groupDto.name();

    log.info("creating group with name {}", groupName);

    if (groupRepository.existsByName(groupName)) {
      throw new ResourceAlreadyExistsException(GROUP_ALREADY_EXISTS_MESSAGE);
    }

    UUID groupId = UUID.randomUUID();

    Group group = new Group(groupId, groupName);
    groupRepository.persist(group);

    GroupMember groupMember =
        new GroupMember(
            UUID.randomUUID(),
            groupId,
            userPrincipal.id(),
            userPrincipal.username(),
            GroupMember.Role.ADMIN);
    groupMemberRepository.persist(groupMember);

    log.info("created group with name {}", groupName);

    return group;
  }

  @Override
  public PageDto<Group> getUserJoinedGroups(UUID userId, int page, int size) {
    log.info("fetching joined groups of user with id {}", userId);

    var query = groupMemberRepository.findByUserId(userId, page, size);
    var ids = query.list().stream().map(GroupMember::getGroupId).toList();
    var groups = new PageDto<>(groupRepository.findByIds(ids), query.count(), query.pageCount());

    log.info("fetched joined groups of user with id {}", userId);

    return groups;
  }

  @Override
  public PanacheQuery<Group> getGroups(String name, int page, int size) {
    log.info("fetching groups with name {}", name);

    var groups = groupRepository.findByName(name, page, size);

    log.info("fetched groups with name {}", name);

    return groups;
  }
}
