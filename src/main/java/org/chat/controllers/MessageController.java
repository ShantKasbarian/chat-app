package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.converters.MessageConverter;
import org.chat.models.MessageRepresentationDto;
import org.chat.models.SubmitMessageDto;
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
    @Context
    private final SecurityContext securityContext;
    private final JsonWebToken token;

    public MessageController(
            MessageService messageService,
            MessageConverter messageConverter,
            SecurityContext securityContext,
            JsonWebToken token
    ) {
        this.messageService = messageService;
        this.messageConverter = messageConverter;
        this.securityContext = securityContext;
        this.token = token;
    }

    @POST
    @Path(("/send"))
    @ResponseStatus(201)
    @Transactional
    public String sendMessage(SubmitMessageDto submitMessageDto) {
        String userId = token.getClaim("userId");
        return messageService.writeMessage(
                messageConverter.convertToEntity(submitMessageDto),
                submitMessageDto.receiverUsername(),
                Long.valueOf(userId)
        );
    }

    @GET
    @Path("/{username}")
    @ResponseStatus(200)
    public List<MessageRepresentationDto> getMessages(@PathParam("username") String username) {
        String userId = token.getClaim("userId");

        return messageService.getMessages(Integer.parseInt(userId), username);
    }

    @POST
    @ResponseStatus(201)
    @Path("/group")
    @Transactional
    public String messageGroup(SubmitMessageDto submitMessageDto) {
        String userId = token.getClaim("userId");

        return messageService.messageGroup(
                messageConverter.convertToEntity(submitMessageDto),
                submitMessageDto.groupName(),
                Long.valueOf(userId)
        );
    }

    @GET
    @ResponseStatus(200)
    @Path("/group/{groupName}")
    public List<MessageRepresentationDto> getGroupMessages(
            @PathParam("groupName") String groupName
    ) {
        String userId = token.getClaim("userId");
        return messageService.getGroupMessages(groupName, Integer.parseInt(userId));
    }
}
