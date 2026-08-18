package org.chat.repository.impl;

import static java.lang.Math.ceil;
import static org.chat.utils.MongoUtils.*;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonNull;
import org.bson.Document;
import org.chat.entity.Message;
import org.chat.model.PageDto;
import org.chat.repository.MessageRepository;

@Slf4j
@ApplicationScoped
public class MessageRepositoryImpl implements MessageRepository {
  private static final String ID = "_id";

  private static final String SENDER_ID = "senderId";

  private static final String SENDER_USERNAME_SNAPSHOT = "senderUsernameSnapshot";

  private static final String TARGET_USER_ID = "targetUserId";

  private static final String TARGET_USERNAME_SNAPSHOT = "targetUsernameSnapshot";

  private static final String GROUP_ID = "groupId";

  private static final String GROUP_NAME_SNAPSHOT = "groupNameSnapshot";

  private static final String TEXT = "text";

  private static final String TYPE = "type";

  private static final String TIME = "time";

  private static final String USERS_MESSAGE_QUERY =
      "("
          + SENDER_ID
          + " = ?1 AND "
          + TARGET_USER_ID
          + " = ?2) OR ("
          + SENDER_ID
          + " = ?2 AND "
          + TARGET_USER_ID
          + " = ?1)";

  @Override
  public PanacheQuery<Message> findByUserIdTargetUserId(
      UUID currentUserId, UUID targetUserId, int page, int size) {
    log.debug(
        "fetching user with id {} target user with id {} messages", currentUserId, targetUserId);

    var messages =
        find(USERS_MESSAGE_QUERY, Sort.by(TIME).descending(), currentUserId, targetUserId)
            .page(Page.of(page, size));

    log.debug(
        "fetched user with id {} target user with id {} messages", currentUserId, targetUserId);

    return messages;
  }

  @Override
  public PanacheQuery<Message> findByGroupId(UUID groupId, int page, int size) {
    log.debug("fetching messages of group with id {}", groupId);

    var messages = find(GROUP_ID, Sort.by(TIME).descending(), groupId).page(Page.of(page, size));

    log.debug("fetched messages of group with id {}", groupId);

    return messages;
  }

  @Override
  public PageDto<Message> findLatestByUserId(UUID id, int page, int size) {
    log.debug("fetching conversations of user with id {}, page {} and size {}", id, page, size);

    long totalElements = countConversations(id);
    long totalPages = (long) ceil((double) totalElements / size);

    if (totalPages - 1 > page) {
      return new PageDto<>(List.of(), totalElements, totalPages);
    }

    List<Document> pipeline = new ArrayList<>(basePipeline(id));
    pipeline.add(sortMessages());
    pipeline.add(groupToConversations());
    pipeline.add(sortConversations());
    pipeline.add(skip(page, size));
    pipeline.add(limit(size));

    var conversations =
        mongoCollection().aggregate(pipeline, Message.class).into(new ArrayList<>());

    log.debug("fetched conversations of user with id {}, page {}, size {}", id, page, size);

    return new PageDto<>(conversations, totalElements, totalPages);
  }

  private long countConversations(UUID id) {
    log.debug("counting conversations of user with id {}", id);

    List<Document> pipeline = new ArrayList<>(basePipeline(id));
    pipeline.add(groupByConversationKey());
    pipeline.add(new Document(COUNT, TOTAL));

    var collection = mongoCollection().withDocumentClass(Document.class);

    Document result = collection.aggregate(pipeline).first();

    log.debug("counted conversations of user with id {}", id);

    return result != null ? result.getInteger(TOTAL) : 0L;
  }

  private List<Document> basePipeline(UUID id) {
    return List.of(matchMessages(id), addConversationKey());
  }

  private Document matchMessages(UUID id) {
    Document senderDoc = new Document(SENDER_ID, id);
    Document targetDoc = new Document(TARGET_USER_ID, id);

    return new Document(MATCH, new Document(OR, List.of(senderDoc, targetDoc)));
  }

  private Document addConversationKey() {
    String sender = "$" + SENDER_ID;
    String target = "$" + TARGET_USER_ID;
    String group = "$" + GROUP_ID;

    Document groupDoc = new Document(NE, List.of(group, BsonNull.VALUE));
    Document senderDoc = new Document(TO_STRING, sender);
    Document targetDoc = new Document(TO_STRING, target);
    Document senderLessDoc = new Document(LT, List.of(sender, target));

    Document orderedSender = new Document(COND, List.of(senderLessDoc, senderDoc, targetDoc));

    Document orderedTarget = new Document(COND, List.of(senderLessDoc, targetDoc, senderDoc));

    Document userKeyString = new Document(CONCAT, List.of(orderedSender, "-", orderedTarget));

    Document userConversationKey = new Document(TO_UUID, userKeyString);

    Document conversationKey = new Document(COND, List.of(groupDoc, group, userConversationKey));

    return new Document(ADD_FIELDS, new Document(CONVERSATION_KEY, conversationKey));
  }

  private Document groupByConversationKey() {
    return new Document(GROUP, new Document(ID, "$" + CONVERSATION_KEY));
  }

  private Document sortMessages() {
    return new Document(SORT, new Document(TIME, -1));
  }

  private Document groupToConversations() {
    var conversationDoc =
        new Document(ID, "$" + CONVERSATION_KEY)
            .append("id", new Document(FIRST, "$" + ID))
            .append(SENDER_ID, new Document(FIRST, "$" + SENDER_ID))
            .append(SENDER_USERNAME_SNAPSHOT, new Document(FIRST, "$" + SENDER_USERNAME_SNAPSHOT))
            .append(TARGET_USER_ID, new Document(FIRST, "$" + TARGET_USER_ID))
            .append(TARGET_USERNAME_SNAPSHOT, new Document(FIRST, "$" + TARGET_USERNAME_SNAPSHOT))
            .append(GROUP_ID, new Document(FIRST, "$" + GROUP_ID))
            .append(GROUP_NAME_SNAPSHOT, new Document(FIRST, "$" + GROUP_NAME_SNAPSHOT))
            .append(TEXT, new Document(FIRST, "$" + TEXT))
            .append(TYPE, new Document(FIRST, "$" + TYPE))
            .append(TIME, new Document(FIRST, "$" + TIME));

    return new Document(GROUP, conversationDoc);
  }

  private Document sortConversations() {
    return new Document(SORT, new Document(TIME, -1));
  }

  private Document skip(int page, int size) {
    return new Document(SKIP, page * size);
  }

  private Document limit(int size) {
    return new Document(LIMIT, size);
  }
}
