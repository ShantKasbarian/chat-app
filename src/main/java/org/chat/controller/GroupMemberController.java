package org.chat.controller;

import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupMemberConverter;
import org.chat.entity.GroupMember;
import org.chat.model.GroupMemberDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.GroupMemberService;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupMemberController {
    private final GroupMemberService groupMemberService;

    private final GroupMemberConverter groupMemberConverter;

    private final UserContext userContext;

    @POST
    @Path("/{groupId}/members")
    @ResponseStatus(201)
    public Response joinGroup(@PathParam("groupId") UUID groupId) {
        log.info("POST /groups/{groupId}/members called");

        var groupUserDto = groupMemberConverter.convertToModel(
                groupMemberService.joinGroup(groupId, userContext.get())
        );

        log.info("POST /groups/{groupId}/members returning a {}", GroupMemberDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(groupUserDto)
                .build();
    }

    @DELETE
    @Path("/{groupId}/members")
    @ResponseStatus(204)
    public void leaveGroup(@PathParam("groupId") UUID groupId) {
        log.info("DELETE /groups/{groupId}/members called");

        groupMemberService.leaveGroup(groupId, userContext.get().id());

        log.info("DELETE /groups/{groupId}/members user left group");
    }

    @PATCH
    @Path("/members/{id}")
    public Response acceptUserToGroup(@PathParam("id") UUID id) {
        log.info("PUT /groups/members/{id} called");

        var groupUserDto = groupMemberConverter.convertToModel(
                groupMemberService.acceptJoinGroup(userContext.get().id(), id)
        );

        log.info("PUT /groups/members/{id} is returning a {}", GroupMemberDto.class.getName());

        return Response.ok(groupUserDto).build();
    }

    @DELETE
    @Path("/members/{id}")
    @ResponseStatus(204)
    public void rejectUserFromGroup(@PathParam("id") UUID id) {
        log.info("DELETE /groups/members/{id} called");

        groupMemberService.rejectJoinGroup(userContext.get().id(), id);

        log.info("DELETE /groups/members/{id} returning a response");
    }

    @GET
    @Path("/{groupId}/members")
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
        log.info("GET /groups/{groupId}/members called");

        var query = groupMemberService.findUsersByRole(groupId, userContext.get().id(), role, page, size);
        var users = query.list()
                .stream()
                .map(groupMemberConverter::convertToModel)
                .toList();
        var pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /groups/{groupId}/members returning a {} of {}", PageDto.class.getName(), GroupMemberDto.class);

        return Response.ok(pageDto).build();
    }
}
