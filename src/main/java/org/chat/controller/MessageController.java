package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.service.impl.MessageServiceImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

import static org.chat.config.JwtService.USER_ID_CLAIM;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class MessageController {
    private final MessageServiceImpl messageService;

    private final MessageConverter messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    @POST
    @Path(("/send"))
    @ResponseStatus(201)
    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        return messageConverter.convertToModel(
                messageService.sendMessage(
                    messageDto.message(),
                    messageDto.recipientId(),
                    token.getClaim(USER_ID_CLAIM)
                )
        );
    }

    @GET
    @Path("/{userId}")
    @ResponseStatus(200)
    public List<MessageDto> getMessages(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size

    ) {
        return messageService.getMessages(token.getClaim(USER_ID_CLAIM), userId, page, size);
    }

    @POST
    @ResponseStatus(201)
    @Path("/group")
    @Transactional
    public GroupMessageDto messageGroup(GroupMessageDto messageDto) {
        return groupMessageConverter.convertToModel(
                messageService.messageGroup(
                    messageDto.message(),
                    messageDto.groupId(),
                    token.getClaim(USER_ID_CLAIM)
                )
        );
    }

    @GET
    @ResponseStatus(200)
    @Path("/group/{groupId}")
    public List<GroupMessageDto> getGroupMessages(
            @PathParam("groupId") String groupId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return messageService.getGroupMessages(groupId, token.getClaim(USER_ID_CLAIM), page, size);
    }
}
