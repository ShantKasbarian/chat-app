package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Message;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.service.MessageService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

import static org.chat.controller.GroupController.UPN_CLAIM;
import static org.chat.service.impl.JwtServiceImpl.USER_ID_CLAIM;

@Slf4j
@RequiredArgsConstructor
@Path("/messages")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageController {
    private final MessageService messageService;

    private final ToModelConverter<MessageDto, Message> messageToModelConverter;

    private final GroupMessageConverter groupMessageConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    @POST
    @Transactional
    public Response sendMessage(@Valid MessageDto messageDto) {
        log.info("POST /messages called");

        var message = messageToModelConverter.convertToModel(
                messageService.sendMessage(
                    messageDto.text(),
                    messageDto.targetUserId(),
                    UUID.fromString(token.getClaim(USER_ID_CLAIM)),
                    token.getClaim(UPN_CLAIM)
                )
        );

        log.info("POST /messages returning a {}", MessageDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response getMessages(
            @PathParam("userId") UUID userId,
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size

    ) {
        log.info("GET /messages/{userId} called");

        var messages = messageService.getMessages(
                UUID.fromString(token.getClaim(USER_ID_CLAIM)), userId, page, size
        )
            .stream()
            .map(messageToModelConverter::convertToModel)
            .toList();

        log.info("GET /messages/{userId} returning a {} of {}", List.class.getName(), MessageDto.class.getName());

        return Response.ok(messages).build();
    }

    @POST
    @Path("/group")
    @Transactional
    public Response messageGroup(@Valid GroupMessageDto messageDto) {
        log.info("POST /messages/group called");

        var message = groupMessageConverter.convertToModel(
                messageService.messageGroup(
                    messageDto.text(),
                    messageDto.groupId(),
                    UUID.fromString(token.getClaim(USER_ID_CLAIM)),
                    token.getClaim(UPN_CLAIM)
                )
        );

        log.info("POST /messages/group returning a {}", MessageDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

    @GET
    @Path("/group/{groupId}")
    public Response getGroupMessages(
            @PathParam("groupId") UUID groupId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        log.info("GET /messages/group/{groupId} called");

        var messages = messageService.getGroupMessages(
                groupId, UUID.fromString(token.getClaim(USER_ID_CLAIM)), page, size
        )
            .stream()
            .map(groupMessageConverter::convertToModel)
            .toList();

        log.info("GET /messages/group/{groupId} returning a {} of {}", List.class.getName(), GroupMessageDto.class.getName());

        return Response.ok(messages).build();
    }
}
