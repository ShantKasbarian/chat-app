package org.chat.service.impl;

import static org.chat.service.impl.GroupServiceImpl.REQUEST_NOT_AUTHORIZED;

import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.entity.Group;
import org.chat.entity.GroupMember;
import org.chat.entity.Message;
import org.chat.entity.User;
import org.chat.exception.ForbiddenException;
import org.chat.exception.ResourceNotFoundException;
import org.chat.model.PageDto;
import org.chat.repository.GroupMemberRepository;
import org.chat.repository.GroupRepository;
import org.chat.repository.MessageRepository;
import org.chat.repository.UserRepository;
import org.chat.security.UserPrincipal;
import org.chat.service.MessageService;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class MessageServiceImpl implements MessageService {
  private static final String USER_NOT_FOUND = "user not found";

  private static final String NOT_MEMBER_OF_GROUP_MESSAGE = "you are not a member of this group";

  private final MessageRepository messageRepository;

  private final UserRepository userRepository;

  private final GroupMemberRepository groupMemberRepository;

  private final GroupRepository groupRepository;

  @Override
  public Message sendMessage(UserPrincipal userPrincipal, String content, UUID targetUserId) {
    log.info("sending message to user with id {}", targetUserId);

    User target =
        userRepository
            .findByIdOptional(targetUserId)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

    Message message =
        new Message(
            UUID.randomUUID(),
            userPrincipal.id(),
            userPrincipal.username(),
            targetUserId,
            target.getUsername(),
            null,
            null,
            content,
            Message.Type.USER,
            Instant.now());

    messageRepository.persist(message);

    log.info("sent message to user with id {}", targetUserId);

    return message;
  }

  @Override
  public PanacheQuery<Message> getMessages(UUID userId, UUID targetUserId, int page, int size) {
    log.info(
        "fetching messages of user with id {} and target user with id {} with page {} and size {}",
        userId,
        targetUserId,
        page,
        size);

    var messages = messageRepository.findByUserIdTargetUserId(userId, targetUserId, page, size);

    log.info(
        "fetched messages of user with id {} and target user with id {} with page {} and size {}",
        userId,
        targetUserId,
        page,
        size);

    return messages;
  }

  @Override
  public Message messageGroup(UserPrincipal userPrincipal, String content, UUID groupId) {
    log.info("sending message to group with id {}", groupId);

    UUID userId = userPrincipal.id();

    GroupMember groupMember =
        groupMemberRepository
            .findByGroupIdUserId(groupId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

    if (groupMember.getRole().equals(GroupMember.Role.PENDING)) {
      throw new ForbiddenException(REQUEST_NOT_AUTHORIZED);
    }

    Group group = groupRepository.findById(groupId);

    Message message =
        new Message(
            UUID.randomUUID(),
            userId,
            userPrincipal.username(),
            null,
            null,
            groupId,
            group.getName(),
            content,
            Message.Type.GROUP,
            Instant.now());

    messageRepository.persist(message);

    log.info("sent message to group with id {}", groupId);

    return message;
  }

  @Override
  public PanacheQuery<Message> getGroupMessages(UUID groupId, UUID userId, int page, int size) {
    log.info("fetching messages of group with id {}, page {} and size {}", groupId, page, size);

    GroupMember groupMember =
        groupMemberRepository
            .findByGroupIdUserId(groupId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_OF_GROUP_MESSAGE));

    if (groupMember.getRole().equals(GroupMember.Role.PENDING)) {
      throw new ForbiddenException(REQUEST_NOT_AUTHORIZED);
    }

    var messages = messageRepository.findByGroupId(groupId, page, size);

    log.info("fetched messages of group with id {}, page {} and size {}", groupId, page, size);

    return messages;
  }

  @Override
  public PageDto<Message> findLatestByUserId(UUID id, int page, int size) {
    log.info("fetching the latest messages of user with id {}, page {}, size {}", id, page, size);

    var messages = messageRepository.findLatestByUserId(id, page, size);

    log.info("fetched the latest messages of user with id {}, page {}, size {}", id, page, size);

    return messages;
  }
}
