package org.chat.repository.impl;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GroupMemberRepositoryImplTest {
  @Inject private GroupMemberRepositoryImpl groupUserRepository;

  @Inject private UserRepositoryImpl userRepository;

  @Inject private GroupRepositoryImpl groupRepository;

  private GroupMember groupMember;

  private User user;

  private Group group;

  @BeforeEach
  void setUp() {
    group = new Group();
    group.setId(UUID.randomUUID());
    group.setName("group");
    groupRepository.persist(group);

    user = new User();
    user.setId(UUID.randomUUID());
    user.setUsername("user");
    user.setPassword("Password123+");
    userRepository.persist(user);

    groupMember = new GroupMember();
    groupMember.setId(UUID.randomUUID());
    groupMember.setGroupId(group.getId());
    groupMember.setUserId(user.getId());
    groupMember.setRole(GroupMember.Role.ADMIN);

    groupUserRepository.persist(groupMember);
  }

  @AfterEach
  void tearDown() {
    groupRepository.delete(group);
    userRepository.delete(user);
    groupUserRepository.delete(groupMember);
  }

  @Test
  void findByGroupIdUserId() {
    Optional<GroupMember> groupMember =
        groupUserRepository.findByGroupIdUserId(group.getId(), user.getId());

    assertNotNull(groupMember);
    assertTrue(groupMember.isPresent());
    assertEquals(group.getId(), groupMember.get().getGroupId());
    assertEquals(user.getId(), groupMember.get().getUserId());
  }

  @Test
  void existsByGroupIdUserId() {
    assertTrue(groupUserRepository.existsByGroupIdUserId(group.getId(), user.getId()));
  }

  @Test
  void findByRole() {
    var groupUsers = groupUserRepository.findByRole(group.getId(), GroupMember.Role.ADMIN, 0, 10);

    assertNotNull(groupUsers);
    assertFalse(groupUsers.list().isEmpty());
  }

  @Test
  void findByUserId() {
    var groupUsers = groupUserRepository.findByUserId(user.getId(), 0, 10);

    assertNotNull(groupUsers);
    assertFalse(groupUsers.list().isEmpty());
  }
}
