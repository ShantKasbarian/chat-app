package org.chat.repository.impl;

import com.mongodb.client.MongoCollection;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonNull;
import org.bson.Document;
import org.chat.entity.Message;
import org.chat.model.ConversationDto;
import org.chat.model.PageDto;
import org.chat.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.ceil;

@Slf4j
@ApplicationScoped
public class MessageRepositoryImpl implements MessageRepository {
    private static final String COLLECTION = "messages";

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

    private static final String USERS_MESSAGE_QUERY = "(" + SENDER_ID + " = ?1 AND " + TARGET_USER_ID + " = ?2) OR (" + SENDER_ID + " = ?2 AND " + TARGET_USER_ID + " = ?1)";

    @Override
    public PanacheQuery<Message> findByUserIdTargetUserId(UUID currentUserId, UUID targetUserId, int page, int size) {
        log.debug("fetching user with id {} target user with id {} messages", currentUserId, targetUserId);

        var messages = find(USERS_MESSAGE_QUERY, Sort.by(TIME).descending(), currentUserId, targetUserId)
                .page(Page.of(page, size));

        log.debug("fetched user with id {} target user with id {} messages", currentUserId, targetUserId);

        return messages;
    }

    @Override
    public PanacheQuery<Message> findByGroupId(UUID groupId, int page, int size) {
        log.debug("fetching messages of group with id {}", groupId);

        var messages = find(GROUP_ID, Sort.by(TIME).descending(), groupId)
                .page(Page.of(page, size));

        log.debug("fetched messages of group with id {}", groupId);

        return messages;
    }

    @Override
    public PageDto<ConversationDto> findConversations(UUID id, int page, int size) {

        List<Document> pipeline = new ArrayList<>(basePipeline(id));

        pipeline.addAll(List.of(
                sortMessages(),
                groupToConversations(id),
                sortConversations(),
                skip(page, size),
                limit(size)
        ));

        var conversations = mongoCollection()
                .aggregate(pipeline, ConversationDto.class)
                .into(new ArrayList<>());

        long count = countConversations(id);

        return new PageDto<>(conversations, count, (long) ceil((double) count / size));
    }

    private long countConversations(UUID id) {
        List<Document> pipeline = new ArrayList<>(basePipeline(id));
        pipeline.add(groupByConversationKey());
        pipeline.add(new Document("$count", "total"));

        MongoCollection<Document> collection =
                mongoCollection().withDocumentClass(Document.class);

        Document result = collection.aggregate(pipeline)
                .first();

        return result != null ? result.getInteger("total") : 0L;
    }

    private List<Document> basePipeline(UUID id) {
        return List.of(matchMessages(id), addConversationKey());
    }

    private Document matchMessages(UUID id) {
        return new Document("$match", new Document("$or", List.of(
                new Document(SENDER_ID, id),
                new Document(TARGET_USER_ID, id)
        )));
    }

    private Document addConversationKey() {
        String sender = "$" + SENDER_ID;
        String target = "$" + TARGET_USER_ID;
        String group = "$" + GROUP_ID;

        Document isGroup = new Document(
                "$ne",
                List.of(group, BsonNull.VALUE)
        );

        // Convert BOTH IDs to STRING FIRST (safe boundary)
        Document senderStr = new Document("$toString", sender);
        Document targetStr = new Document("$toString", target);

        // Determine stable ordering using ORIGINAL UUIDs (safe)
        Document isSenderLess = new Document(
                "$lt",
                List.of(sender, target)
        );

        Document orderedSender = new Document(
                "$cond",
                List.of(
                        isSenderLess,
                        senderStr,
                        targetStr
                )
        );

        Document orderedTarget = new Document(
                "$cond",
                List.of(
                        isSenderLess,
                        targetStr,
                        senderStr
                )
        );

        // SAFE CONCAT (ONLY STRINGS)
        Document userKeyString = new Document(
                "$concat",
                List.of(
                        orderedSender,
                        "-",
                        orderedTarget
                )
        );

        // Convert final string → UUID
        Document userConversationKey = new Document(
                "$toUUID",
                userKeyString
        );

        Document conversationKey = new Document(
                "$cond",
                List.of(
                        isGroup,
                        group,
                        userConversationKey
                )
        );

        return new Document(
                "$addFields",
                new Document("conversationKey", conversationKey)
        );
    }

    private Document groupByConversationKey() {
        return new Document("$group",
                new Document(ID, "$conversationKey")
        );
    }

    private Document sortMessages() {
        return new Document("$sort", new Document(TIME, -1));
    }

    private Document groupToConversations(UUID id) {
        String senderField = "$" + SENDER_ID;

        Document isSender = new Document(
                "$eq",
                List.of(senderField, id)
        );

        Document nameResolver = new Document(
                "$cond",
                List.of(
                        new Document("$eq", List.of("$" + TYPE, Message.Type.GROUP.name())),
                        "$" + GROUP_NAME_SNAPSHOT,
                        new Document("$cond", List.of(
                                isSender,
                                "$" + TARGET_USERNAME_SNAPSHOT,
                                "$" + SENDER_USERNAME_SNAPSHOT
                        ))
                )
        );

        return new Document("$group",
                new Document("_id", "$conversationKey")
                        .append("id", new Document("$first", "$_id"))
                        .append("message", new Document("$first", "$" + TEXT))
                        .append("messageType", new Document("$first", "$" + TYPE))
                        .append("time", new Document("$first", "$" + TIME))
                        .append("name", new Document("$first", nameResolver))
        );
    }

    private Document sortConversations() {
        return new Document("$sort", new Document(TIME, -1));
    }

    private Document skip(int page, int size) {
        return new Document("$skip", page * size);
    }

    private Document limit(int size) {
        return new Document("$limit", size);
    }
}
