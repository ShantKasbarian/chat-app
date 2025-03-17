package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.converters.GroupMessageConverter;
import org.chat.converters.MessageConverter;
import org.chat.models.GroupMessageDto;
import org.chat.models.MessageDto;
import org.chat.services.MessageService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class MessageController {
    private final MessageService messageService;

    private final MessageConverter messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    public MessageController(
            MessageService messageService,
            MessageConverter messageConverter,
            GroupMessageConverter groupMessageConverter,
            SecurityContext securityContext,
            JsonWebToken token
    ) {
        this.messageService = messageService;
        this.messageConverter = messageConverter;
        this.groupMessageConverter = groupMessageConverter;
        this.securityContext = securityContext;
        this.token = token;
    }

    @POST
    @Path(("/send"))
    @ResponseStatus(201)
    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        return messageConverter.convertToModel(
                messageService.writeMessage(
                    messageDto.message(),
                    messageDto.recipientUsername(),
                    token.getClaim("userId")
                )
        );
    }

    @GET
    @Path("/{username}")
    @ResponseStatus(200)
    public List<MessageDto> getMessages(@PathParam("username") String username) {
        return messageService.getMessages(token.getClaim("userId"), username);
    }

    @POST
    @ResponseStatus(201)
    @Path("/group")
    @Transactional
    public GroupMessageDto messageGroup(GroupMessageDto messageDto) {
        return groupMessageConverter.convertToModel(
                messageService.messageGroup(
                    messageDto.message(),
                    messageDto.groupName(),
                    token.getClaim("userId")
                )
        );
    }

    @GET
    @ResponseStatus(200)
    @Path("/group/{groupName}")
    public List<GroupMessageDto> getGroupMessages(
            @PathParam("groupName") String groupName
    ) {
        return messageService.getGroupMessages(groupName, token.getClaim("userId"));
    }
}
