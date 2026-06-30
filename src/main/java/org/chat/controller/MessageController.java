package org.chat.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.model.GroupMessageDto;
import org.chat.model.MessageDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.MessageService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageController {
    private final MessageService messageService;

    private final MessageConverter messageConverter;

    private final GroupMessageConverter groupMessageConverter;

    private final UserContext userContext;

    @POST
    public Response sendMessage(@Valid MessageDto messageDto) {
        log.info("POST /messages called");

        var message = messageConverter.convertToModel(
            messageService.sendMessage(
                userContext.get(), messageDto.text(), messageDto.targetUserId()
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
        log.info("GET /messages/{} called with page {} and size {}", userId, page, size);

        var query = messageService.getMessages(userContext.get().id(), userId, page, size);
        var messages = query.list()
            .stream()
            .map(messageConverter::convertToModel)
            .toList();

        var pageDto = new PageDto<>(messages, query.count(), query.pageCount());

        log.info("GET /messages/{} returning a {} of {} with page {} and size {}", userId, PageDto.class.getName(), MessageDto.class.getName(), page, size);

        return Response.ok(pageDto).build();
    }

    @POST
    @Path("/groups")
    public Response messageGroup(@Valid GroupMessageDto messageDto) {
        log.info("POST /messages/groups called");

        var message = groupMessageConverter.convertToModel(
                messageService.messageGroup(
                        userContext.get(),
                        messageDto.text(),
                        messageDto.groupId()
                )
        );

        log.info("POST /messages/groups returning a {}", MessageDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(message)
                .build();
    }

    @GET
    @Path("/groups/{groupId}")
    public Response getGroupMessages(
            @PathParam("groupId") UUID groupId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        log.info("GET /messages/groups/{} called with page {}, size {}", groupId, page, size);

        var query = messageService.getGroupMessages(groupId, userContext.get().id(), page, size);
        var messages = query.list()
            .stream()
            .map(groupMessageConverter::convertToModel)
            .toList();

        var pageDto = new PageDto<>(messages, query.count(), query.pageCount());

        log.info("GET /messages/groups/{} returning a {} of {} with page {}, size {}", groupId, PageDto.class.getName(), GroupMessageDto.class.getName(), page, size);

        return Response.ok(pageDto).build();
    }
}
