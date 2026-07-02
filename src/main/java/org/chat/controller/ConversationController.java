package org.chat.controller;

import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ConversationConverter;
import org.chat.model.ConversationDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.MessageService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/conversations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
@Tag(name = "conversations", description = "conversations management")
public class ConversationController {
    private final MessageService messageService;

    private final ConversationConverter conversationConverter;

    private final UserContext userContext;

    @GET
    @Operation(summary = "get conversations", description = "returns a list of the current user's conversations")
    @APIResponse(
            responseCode = "200",
            description = "conversations fetched successfully",
            content = @Content(schema = @Schema(implementation = PageDto.class))
    )
    public Response getConversations(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("10") @Min(1) int size
    ) {
        UUID id = userContext.get().id();

        log.info("GET /conversations called with user id {}, page {}, size {}", id, page, size);

        var messages = messageService.findLatestByUserId(id, page, size);
        var conversations = messages.content()
                .stream()
                .map(message -> conversationConverter.convertToModel(message, id))
                .toList();
        var pageDto = new PageDto<>(conversations, messages.totalElements(), messages.totalPages());

        log.info("GET /conversations returning a {} of {} with user id {}, page {}, size {}", PageDto.class.getName(), ConversationDto.class.getName(), id, page, size);

        return Response.ok()
                .entity(pageDto)
                .build();
    }
}
