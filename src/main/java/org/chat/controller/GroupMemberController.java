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
        log.info("POST /groups/{}/members called", groupId);

        var groupUserDto = groupMemberConverter.convertToModel(
                groupMemberService.joinGroup(groupId, userContext.get())
        );

        log.info("POST /groups/{}/members returning a {}", groupId, GroupMemberDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(groupUserDto)
                .build();
    }

    @DELETE
    @Path("/{groupId}/members")
    @ResponseStatus(204)
    public void leaveGroup(@PathParam("groupId") UUID groupId) {
        log.info("DELETE /groups/{}/members called", groupId);

        groupMemberService.leaveGroup(groupId, userContext.get().id());

        log.info("DELETE /groups/{}/members operation successful", groupId);
    }

    @PATCH
    @Path("/members/{id}")
    public Response acceptJoinRequest(@PathParam("id") UUID id) {
        log.info("PATCH /groups/members/{} called", id);

        var groupUserDto = groupMemberConverter.convertToModel(
                groupMemberService.acceptJoinRequest(userContext.get().id(), id)
        );

        log.info("PATCH /groups/members/{} is returning a {}", id, GroupMemberDto.class.getName());

        return Response.ok(groupUserDto).build();
    }

    @DELETE
    @Path("/members/{id}")
    @ResponseStatus(204)
    public void rejectJoinRequest(@PathParam("id") UUID id) {
        log.info("DELETE /groups/members/{} called", id);

        groupMemberService.rejectJoinRequest(userContext.get().id(), id);

        log.info("DELETE /groups/members/{} operation successful", id);
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
        log.info("GET /groups/{}/members called with page {} and size {}", groupId, page, size);

        var query = groupMemberService.findUsersByRole(groupId, userContext.get().id(), role, page, size);
        var users = query.list()
                .stream()
                .map(groupMemberConverter::convertToModel)
                .toList();
        var pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /groups/{}/members returning a {} of {} with page {} and size {}", groupId, PageDto.class.getName(), GroupMemberDto.class, page, size);

        return Response.ok(pageDto).build();
    }
}
