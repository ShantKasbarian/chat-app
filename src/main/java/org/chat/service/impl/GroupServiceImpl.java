package org.chat.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.exception.*;
import org.chat.model.PageDto;
import org.chat.repository.GroupRepository;
import org.chat.repository.GroupUserRepository;
import org.chat.security.UserPrincipal;
import org.chat.service.GroupService;

import java.util.*;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class GroupServiceImpl implements GroupService {
    static final String REQUEST_NOT_AUTHORIZED = "You do not have the necessary permissions to perform this request";

    private static final String GROUP_ALREADY_EXISTS_MESSAGE = "Group already exists";

    private final GroupRepository groupRepository;

    private final GroupUserRepository groupUserRepository;

    @Override
    @Transactional
    public Group createGroup(Group group, UserPrincipal userPrincipal) {
        String groupName = group.getName();

        log.info("creating group with name {}", groupName);

        if (groupRepository.existsByName(group.getName())) {
            throw new ResourceAlreadyExistsException(GROUP_ALREADY_EXISTS_MESSAGE);
        }

        UUID groupId = UUID.randomUUID();
        group.setId(groupId);
        groupRepository.persist(group);

        GroupUser groupUser = new GroupUser(UUID.randomUUID(), groupId, userPrincipal.id(), userPrincipal.username(), GroupUser.Role.ADMIN);
        groupUserRepository.persist(groupUser);

        log.info("created group with name {}", groupName);

        return group;
    }

    @Override
    public PageDto<Group> getUserJoinedGroups(UUID userId, int page, int size) {
        log.info("fetching joined groups of user with id {}", userId);

        var query = groupUserRepository.findByUserId(userId, page, size);
        var ids = query.list()
                .stream()
                .map(GroupUser::getId)
                .toList();
        var groups = new PageDto<>(groupRepository.findByIds(ids), query.count(), query.pageCount());

        log.info("fetched joined groups of user with id {}", userId);

        return groups;
    }

    @Override
    public PanacheQuery<Group> getGroups(String groupName, int page, int size) {
        log.info("fetching groups with name {}", groupName);

        var groups = groupRepository.findByName(groupName, page, size);

        log.info("fetched groups with name {}", groupName);

        return groups;
    }
}
