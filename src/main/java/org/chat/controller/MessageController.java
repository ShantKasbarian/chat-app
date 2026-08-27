package org.chat.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.chat.converter.GroupMessageConverter;
import org.chat.converter.MessageConverter;
import org.chat.model.*;
import org.chat.security.UserContext;
import org.chat.service.MessageService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequiredArgsConstructor
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
@Tag(name = "messages", description = "message management")
public class MessageController {
  private final MessageService messageService;

  private final MessageConverter messageConverter;

  private final GroupMessageConverter groupMessageConverter;

  private final UserContext userContext;

  @POST
  @Operation(summary = "send message to user", description = "returns a new message")
  @APIResponse(
      responseCode = "201",
      description = "message created",
      content = @Content(schema = @Schema(implementation = MessageDto.class)))
  @APIResponse(
      responseCode = "400",
      description = "invalid values in request body",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  @APIResponse(
      responseCode = "404",
      description = "target user not found",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  public Response sendMessage(@Valid MessageDto messageDto) {
    var message =
        messageConverter.convertToModel(messageService.sendMessage(messageDto, userContext.get()));

    return Response.status(Response.Status.CREATED).entity(message).build();
  }

  @GET
  @Path("/{userId}")
  @Operation(
      summary = "get messages between current and target user",
      description = "returns a list of messages")
  @APIResponse(
      responseCode = "200",
      description = "messages fetched successfully",
      content = @Content(schema = @Schema(implementation = PageDto.class)))
  public Response getMessages(
      @PathParam("userId") UUID userId,
      @QueryParam("page") @DefaultValue("0") @Min(value = 0, message = "page must be at least 0")
          int page,
      @QueryParam("size") @DefaultValue("10") @Min(value = 1, message = "size must be at least 1")
          int size) {
    var query = messageService.getMessages(userContext.get().id(), userId, page, size);
    var messages = query.list().stream().map(messageConverter::convertToModel).toList();
    var pageDto = new PageDto<>(messages, query.count(), query.pageCount());

    return Response.ok(pageDto).build();
  }

  @POST
  @Path("/groups")
  @Operation(summary = "send message to group", description = "returns a new message")
  @APIResponse(
      responseCode = "201",
      description = "messages created successfully",
      content = @Content(schema = @Schema(implementation = GroupMessageDto.class)))
  @APIResponse(
      responseCode = "400",
      description = "invalid values in request body",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  @APIResponse(
      responseCode = "403",
      description = "current user is not ADMIN or MEMBER",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  @APIResponse(
      responseCode = "404",
      description = "current user is not a member of group",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  public Response messageGroup(@Valid GroupMessageDto groupMessageDto) {
    var message =
        groupMessageConverter.convertToModel(
            messageService.messageGroup(groupMessageDto, userContext.get()));

    return Response.status(Response.Status.CREATED).entity(message).build();
  }

  @GET
  @Path("/groups/{groupId}")
  @Operation(summary = "get group messages", description = "returns a list of messages")
  @APIResponse(
      responseCode = "200",
      description = "messages fetched successfully",
      content = @Content(schema = @Schema(implementation = GroupMessageDto.class)))
  @APIResponse(
      responseCode = "403",
      description = "current user is not ADMIN or MEMBER",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  @APIResponse(
      responseCode = "404",
      description = "current user is not a member of group",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  public Response getGroupMessages(
      @PathParam("groupId") UUID groupId,
      @QueryParam("page") @DefaultValue("0") int page,
      @QueryParam("size") @DefaultValue("10") int size) {
    var query = messageService.getGroupMessages(groupId, userContext.get().id(), page, size);
    var messages = query.list().stream().map(groupMessageConverter::convertToModel).toList();
    var pageDto = new PageDto<>(messages, query.count(), query.pageCount());

    return Response.ok(pageDto).build();
  }
}
