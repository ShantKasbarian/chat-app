package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupUserConverter;
import org.chat.entity.GroupUser;
import org.chat.model.GroupUserDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.GroupUserService;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/groups")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupUserController {
    private final GroupUserService groupUserService;

    private final GroupUserConverter groupUserConverter;

    private final UserContext userContext;

    @POST
    @Path("/{groupId}/members")
    @ResponseStatus(201)
    @Transactional
    public Response joinGroup(@PathParam("groupId") UUID groupId) {
        log.info("POST /groups/{groupId}/members called");

        var groupUserDto = groupUserConverter.convertToModel(
                groupUserService.joinGroup(groupId, userContext.get())
        );

        log.info("POST /groups/{groupId}/members returning a {}", GroupUserDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(groupUserDto)
                .build();
    }

    @DELETE
    @Path("/{groupId}/members")
    @ResponseStatus(204)
    @Transactional
    public void leaveGroup(@PathParam("groupId") UUID groupId) {
        log.info("DELETE /groups/{groupId}/members called");

        groupUserService.leaveGroup(groupId, userContext.get().id());

        log.info("DELETE /groups/{groupId}/members user left group");
    }

    @PATCH
    @Path("/members/{id}")
    @Transactional
    public Response acceptUserToGroup(@PathParam("id") UUID id) {
        log.info("PUT /groups/members/{id} called");

        var groupUserDto = groupUserConverter.convertToModel(
                groupUserService.acceptJoinGroup(userContext.get().id(), id)
        );

        log.info("PUT /groups/members/{id} is returning a {}", GroupUserDto.class.getName());

        return Response.ok(groupUserDto).build();
    }

    @DELETE
    @Path("/members/{id}")
    @ResponseStatus(204)
    @Transactional
    public void rejectUserFromGroup(@PathParam("id") UUID id) {
        log.info("DELETE /groups/members/{id} called");

        groupUserService.rejectJoinGroup(userContext.get().id(), id);

        log.info("DELETE /groups/members/{id} returning a response");
    }

    @GET
    @Path("/{groupId}/members")
    public Response getUsersByRole(
            @PathParam("groupId") UUID groupId,
            @QueryParam("role")
            @DefaultValue("MEMBER")
            GroupUser.Role role,
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

        var query = groupUserService.findUsersByRole(groupId, userContext.get().id(), role, page, size);
        var users = query.list()
                .stream()
                .map(groupUserConverter::convertToModel)
                .toList();
        var pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /groups/{groupId}/members returning a {} of {}", PageDto.class.getName(), GroupUserDto.class);

        return Response.ok(pageDto).build();
    }
}
