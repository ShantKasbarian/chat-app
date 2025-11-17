package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Message;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.service.MessageService;
import org.chat.service.impl.MessageServiceImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

import static org.chat.config.JwtService.USER_ID_CLAIM;

@Slf4j
@RequiredArgsConstructor
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class MessageController {
    private final MessageService messageService;

    private final ToModelConverter<MessageDto, Message> messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    @POST
    @Transactional
    public Response sendMessage(MessageDto messageDto) {
        log.info("/messages with POST called");

        var message = messageConverter.convertToModel(
                messageService.sendMessage(
                    messageDto.message(),
                    messageDto.recipientId(),
                    token.getClaim(USER_ID_CLAIM)
                )
        );

        log.info("/messages with POST returning a {}", MessageDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

    @GET
    @Path("/{userId}")
    public Response getMessages(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size

    ) {
        log.info("/messages/{userId} with GET called");

        var messages = messageService.getMessages(token.getClaim(USER_ID_CLAIM), userId, page, size);

        log.info("/messages/{userId} returning a {} of {}", List.class.getName(), MessageDto.class.getName());

        return Response.ok(messages).build();
    }

    @POST
    @Path("/group")
    @Transactional
    public Response messageGroup(GroupMessageDto messageDto) {
        log.info("/messages/group with POST called");

        var message = groupMessageConverter.convertToModel(
                messageService.messageGroup(
                    messageDto.message(),
                    messageDto.groupId(),
                    token.getClaim(USER_ID_CLAIM)
                )
        );

        log.info("/messages/group returning a {}", MessageDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

    @GET
    @Path("/group/{groupId}")
    public Response getGroupMessages(
            @PathParam("groupId") String groupId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        log.info("/messages/group/{groupId} with GET called");

        var messages = messageService.getGroupMessages(groupId, token.getClaim(USER_ID_CLAIM), page, size);

        log.info("/messages/group/{groupId} with GET returning a {} of {}", List.class.getName(), GroupMessageDto.class.getName());

        return Response.ok(messages).build();
    }
}
