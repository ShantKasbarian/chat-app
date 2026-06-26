package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.GroupConverter;
import org.chat.converter.GroupUserConverter;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/groups")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupController {
    private final GroupService groupService;

    private final GroupConverter groupConverter;

    private final GroupUserConverter groupUserConverter;

    private final UserContext userContext;

    @POST
    @Transactional
    public Response create(@Context JsonWebToken jsonWebToken, @Valid GroupDto groupDto) {
        log.info("POST /groups called");

        var group = groupConverter.convertToModel(
            groupService.createGroup(
                groupConverter.convertToEntity(groupDto),
                userContext.get()
            )
        );

        log.info("POST /groups returning a {}", GroupDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(group)
                .build();
    }

    @POST
    @Path("/{groupId}/join")
    @ResponseStatus(201)
    @Transactional
    public Response joinGroup(@PathParam("groupId") UUID groupId) {
        log.info("POST /groups/{groupId}/join called");

        var groupUserDto = groupUserConverter.convertToModel(
                groupService.joinGroup(groupId, userContext.get())
        );

        log.info("POST /groups/{groupId}/join returning a {}", GroupUserDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(groupUserDto)
                .build();
    }

    @DELETE
    @Path("/{groupId}/leave")
    @ResponseStatus(204)
    @Transactional
    public void leaveGroup(@PathParam("groupId") UUID groupId) {
        log.info("DELETE /groups/{groupId}/leave called");

        groupService.leaveGroup(groupId, userContext.get().id());

        log.info("DELETE /groups/{groupId}/leave user left group");
    }

    @PATCH
    @Path("/accept/{groupUserId}")
    @Transactional
    public Response acceptUserToGroup(@PathParam("groupUserId") UUID groupUserId) {
        log.info("PUT /groups/accept/{groupUserId} called");

        var groupUserDto = groupUserConverter.convertToModel(
                groupService.acceptJoinGroup(userContext.get().id(), groupUserId)
        );

        log.info("/groups/accept/{groupUserId} with PUT returning a {}", GroupUserDto.class.getName());

        return Response.ok(groupUserDto).build();
    }

    @DELETE
    @Path("/reject/{groupUserId}")
    @ResponseStatus(204)
    @Transactional
    public void rejectUserFromGroup(@PathParam("groupUserId") UUID groupUserId) {
        log.info("DELETE /groups/reject/{groupUserId} called");

        groupService.rejectJoinGroup(userContext.get().id(), groupUserId);

        log.info("DELETE /groups/reject/{groupUserId} returning a response");
    }

    @GET
    @Path("/{groupId}/waiting/users")
    public Response getWaitingUsers(
            @PathParam("groupId") UUID groupId,
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size
    ) {
        log.info("GET /groups/{groupId}/waiting/users called");

        var query = groupService.findUsersWithPendingRole(groupId, userContext.get().id(), page, size);
        var users = query.list()
                .stream()
                .map(groupUserConverter::convertToModel)
                .toList();
        var pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /groups/{groupId}/waiting/users returning a {} of {}", PageDto.class.getName(), GroupUserDto.class);

        return Response.ok(pageDto).build();
    }

    @GET
    @Path("/joined")
    public Response getJoinedGroups(
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size
    ) {
        log.info("GET /groups/joined called");

        var pageDto = groupService.getUserJoinedGroups(userContext.get().id(), page, size);
        var groups = pageDto.content()
                .stream()
                .map(groupConverter::convertToModel)
                .toList();

        log.info("GET /groups/joined returning a {} of {}", PageDto.class.getName(), GroupDto.class.getName());

        return Response.ok(new PageDto<>(groups, page, size)).build();
    }

    @GET
    @Path("/{groupName}")
    public Response getGroups(
            @PathParam("groupName") String groupName,
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size
    ) {
        log.info("GET /groups/{groupName} called");

        var query = groupService.getGroups(groupName, page, size);
        var groups = query.list()
                .stream()
                .map(groupConverter::convertToModel)
                .toList();
        var pageDto = new PageDto<>(groups, page, size);

        log.info("GET /groups/{groupName} returning a {} of {}", PageDto.class.getName(), GroupDto.class.getName());

        return Response.ok(pageDto).build();
    }
}
