package org.chat.controllers;

import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.converters.MessageConverter;
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
@PermitAll
public class MessageController {
    private final MessageService messageService;
    private final MessageConverter messageConverter;
    @Context
    private final SecurityContext securityContext;
    private final JsonWebToken jwt;

    public MessageController(
            MessageService messageService,
            MessageConverter messageConverter,
            SecurityContext securityContext,
            JsonWebToken jwt
    ) {
        this.messageService = messageService;
        this.messageConverter = messageConverter;
        this.securityContext = securityContext;
        this.jwt = jwt;
    }

    @POST
    @Path(("/send"))
    @ResponseStatus(201)
    @Transactional
    public String sendMessage(MessageDto messageDto) {
//        System.out.println("id = " + jwt.getClaim("id").toString());
//        messageDto.setSenderId(jwt.getClaim("id"));
        return messageService.writeMessage(
                messageConverter.convertToEntity(messageDto),
                messageDto.getReceiverUsername()
        );
    }

    @GET
    @ResponseStatus(200)
    public List<MessageDto> getMessages(UserDto userDto) {
        return messageService.getMessages(userDto.getId(), userDto.getUsername())
                .stream()
                .map(message -> {
                    MessageDto messageDto = messageConverter.convertToModel(message);
                    messageDto.setReceiverUsername(userDto.getUsername());
                    return messageDto;
                })
                .collect(Collectors.toList());
    }
}
