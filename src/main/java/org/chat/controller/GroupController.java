package org.chat.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.chat.converter.GroupConverter;
import org.chat.model.ErrorMessageDto;
import org.chat.model.GroupDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.GroupService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequiredArgsConstructor
@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
@Tag(name = "groups", description = "group management")
public class GroupController {
  private final GroupService groupService;

  private final GroupConverter groupConverter;

  private final UserContext userContext;

  @POST
  @Operation(summary = "create group", description = "returns a new group")
  @APIResponse(
      responseCode = "201",
      description = "group created",
      content = @Content(schema = @Schema(implementation = GroupDto.class)))
  @APIResponse(
      responseCode = "400",
      description = "invalid values in request body",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  @APIResponse(
      responseCode = "409",
      description = "group with given name already exists",
      content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
  public Response create(@Valid GroupDto groupDto) {
    var group = groupService.createGroup(groupDto, userContext.get());
    var responseDto = groupConverter.convertToModel(group);

    return Response.status(Response.Status.CREATED).entity(responseDto).build();
  }

  @GET
  @Path("/me")
  @Operation(
      summary = "get current users' joined groups",
      description = "returns current users' joined groups")
  @APIResponse(
      responseCode = "200",
      description = "groups fetched successfully",
      content = @Content(schema = @Schema(implementation = PageDto.class)))
  public Response getJoinedGroups(
      @QueryParam("page") @DefaultValue("0") @Min(value = 0, message = "page must be at least 0")
          int page,
      @QueryParam("size") @DefaultValue("10") @Min(value = 1, message = "size must be at least 1")
          int size) {
    var pageDto = groupService.getUserJoinedGroups(userContext.get().id(), page, size);
    var groups = pageDto.content().stream().map(groupConverter::convertToModel).toList();

    return Response.ok(new PageDto<>(groups, page, size)).build();
  }

  @GET
  @Path("/{name}")
  @Operation(
      summary = "get groups by name",
      description = "returns groups with name similar to inputted name")
  @APIResponse(
      responseCode = "200",
      description = "groups fetched successfully",
      content = @Content(schema = @Schema(implementation = PageDto.class)))
  public Response getGroups(
      @PathParam("name") String name,
      @QueryParam("page") @DefaultValue("0") @Min(value = 0, message = "page must be at least 0")
          int page,
      @QueryParam("size") @DefaultValue("10") @Min(value = 1, message = "size must be at least 1")
          int size) {
    var query = groupService.getGroups(name, page, size);
    var groups = query.list().stream().map(groupConverter::convertToModel).toList();
    var pageDto = new PageDto<>(groups, page, size);

    return Response.ok(pageDto).build();
  }
}
