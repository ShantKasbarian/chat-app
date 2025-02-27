package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.converters.MessageConverter;
import org.chat.models.GroupMessageDto;
import org.chat.models.MessageDto;
import org.chat.models.UserDto;
import org.chat.services.MessageService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

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
    public String sendMessage(MessageDto messageDto) {
        String userId = token.getClaim("userId");
        messageDto.setSenderId(Integer.parseInt(userId));
        return messageService.writeMessage(
                messageConverter.convertToEntity(messageDto),
                messageDto.getReceiverUsername()
        );
    }

    @GET
    @ResponseStatus(200)
    public List<MessageDto> getMessages(UserDto userDto) {
        String userId = token.getClaim("userId");

        return messageService.getMessages(Integer.parseInt(userId), userDto.getUsername())
                .stream()
                .map(message -> {
                    MessageDto messageDto = messageConverter.convertToModel(message);
                    messageDto.setReceiverUsername(userDto.getUsername());
                    return messageDto;
                })
                .collect(Collectors.toList());
    }

    @POST
    @ResponseStatus(201)
    @Path("/group")
    @Transactional
    public String messageGroup(MessageDto messageDto) {
        String userId = token.getClaim("userId");
        messageDto.setSenderId(Integer.parseInt(userId));
        return messageService.messageGroup(
                messageConverter.convertToEntity(messageDto),
                messageDto.getGroupName()
        );
    }

    @GET
    @ResponseStatus(200)
    @Path("/group/{groupName}")
    public List<GroupMessageDto> getGroupMessages(
            @PathParam("groupName") String groupName
    ) {
        String userId = token.getClaim("userId");
        return messageService.getGroupMessages(groupName, Integer.parseInt(userId));
    }
}
