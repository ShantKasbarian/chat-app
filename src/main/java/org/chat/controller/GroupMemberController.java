package org.chat.controller;

import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupMemberConverter;
import org.chat.entity.GroupMember;
import org.chat.model.ErrorMessageDto;
import org.chat.model.GroupMemberDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.GroupMemberService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
@Tag(name = "group members", description = "group members management")
public class GroupMemberController {
    private final GroupMemberService groupMemberService;

    private final GroupMemberConverter groupMemberConverter;

    private final UserContext userContext;

    @POST
    @Path("/{groupId}/members")
    @Operation(summary = "create request to join group", description = "returns a new group member with PENDING role")
    @APIResponse(
            responseCode = "201",
            description = "group member created",
            content = @Content(schema = @Schema(implementation = GroupMemberDto.class))
    )
    @APIResponse(
            responseCode = "409",
            description = "group member with current user id already exists",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    public Response joinGroup(@PathParam("groupId") UUID groupId) {
        log.info("POST /groups/{}/members called", groupId);

        var groupMemberDto = groupMemberConverter.convertToModel(
                groupMemberService.joinGroup(groupId, userContext.get())
        );

        log.info("POST /groups/{}/members returning a {}", groupId, GroupMemberDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(groupMemberDto)
                .build();
    }

    @DELETE
    @Path("/{groupId}/members")
    @Operation(summary = "leave group", description = "deletes group member")
    @APIResponse(responseCode = "204", description = "group member successfully deleted")
    @APIResponse(
            responseCode = "404",
            description = "group member with current user id not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    public Response leaveGroup(@PathParam("groupId") UUID groupId) {
        log.info("DELETE /groups/{}/members called", groupId);

        groupMemberService.leaveGroup(groupId, userContext.get().id());

        log.info("DELETE /groups/{}/members operation successful", groupId);

        return Response.noContent()
                .build();
    }

    @PATCH
    @Path("/members/{id}")
    @Operation(summary = "accept join group request", description = "updates group member role to MEMBER")
    @APIResponse(
            responseCode = "200",
            description = "group member role successfully updated",
            content = @Content(schema = @Schema(implementation = GroupMemberDto.class))
    )
    @APIResponse(
            responseCode = "403",
            description = "current user is not the groups' admin",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "group member with current user id or group member id not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    public Response acceptJoinRequest(@PathParam("id") UUID id) {
        log.info("PATCH /groups/members/{} called", id);

        var groupMemberDto = groupMemberConverter.convertToModel(
                groupMemberService.acceptJoinRequest(userContext.get().id(), id)
        );

        log.info("PATCH /groups/members/{} is returning a {}", id, GroupMemberDto.class.getName());

        return Response.ok(groupMemberDto)
                .build();
    }

    @DELETE
    @Path("/members/{id}")
    @Operation(summary = "reject request to join group", description = "deletes group member")
    @APIResponse(responseCode = "204", description = "group member successfully deleted")
    @APIResponse(
            responseCode = "403",
            description = "current user is not ADMIN or group member role is not PENDING",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "group member with current user id or group member id not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    public Response rejectJoinRequest(@PathParam("id") UUID id) {
        log.info("DELETE /groups/members/{} called", id);

        groupMemberService.rejectJoinRequest(userContext.get().id(), id);

        log.info("DELETE /groups/members/{} operation successful", id);

        return Response.noContent()
                .build();
    }

    @GET
    @Path("/{groupId}/members")
    @Operation(summary = "get group members by role", description = "returns a list of group members with the given role")
    @APIResponse(
            responseCode = "200",
            description = "group members fetched successfully",
            content = @Content(schema = @Schema(implementation = PageDto.class))
    )
    public Response getUsersByRole(
            @PathParam("groupId") UUID groupId,
            @QueryParam("role")
            @DefaultValue("MEMBER")
            GroupMember.Role role,
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size
    ) {
        log.info("GET /groups/{}/members called with page {} and size {}", groupId, page, size);

        var query = groupMemberService.findUsersByRole(groupId, userContext.get().id(), role, page, size);
        var users = query.list()
                .stream()
                .map(groupMemberConverter::convertToModel)
                .toList();
        var pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /groups/{}/members returning a {} of {} with page {} and size {}", groupId, PageDto.class.getName(), GroupMemberDto.class, page, size);

        return Response.ok(pageDto)
                .build();
    }
}
