package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.chat.converters.GroupConverter;
import org.chat.converters.GroupUserConverter;
import org.chat.models.GroupDto;
import org.chat.models.GroupUserDto;
import org.chat.services.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    private final GroupConverter groupConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    private final GroupUserConverter groupUserConverter;

    @POST
    @Path("/create")
    @ResponseStatus(201)
    @Transactional
    public GroupDto create(GroupDto groupDto) {
        return groupConverter.convertToModel(
                groupService.createGroup(
                    groupConverter.convertToEntity(groupDto),
                    groupDto.getCreators(),
                    token.getClaim("userId")
                )
        );
    }

    @POST
    @Path("/{groupId}/join")
    @ResponseStatus(201)
    @Transactional
    public GroupUserDto joinGroup(@PathParam("groupId") String groupId) {
        return groupUserConverter.convertToModel(
                groupService.joinGroup(groupId, token.getClaim("userId"))
        );
    }

    @DELETE
    @Path("/{groupId}/leave")
    @ResponseStatus(204)
    @Transactional
    public String leaveGroup(@PathParam("groupId") String groupId) {
        return groupService.leaveGroup(groupId, token.getClaim("userId"));
    }

    @PUT
    @Path("/{groupId}/accept/user/{userId}")
    @ResponseStatus(200)
    @Transactional
    public GroupUserDto acceptUserToGroup(
            @PathParam("groupId") String groupId,
            @PathParam("userId") String userId
    ) {
        return groupUserConverter.convertToModel(
                groupService.acceptToGroup(
                    groupId,
                    token.getClaim("userId"),
                    userId
                )
        );
    }

    @DELETE
    @Path("/{groupId}/reject/user/{userId}")
    @ResponseStatus(204)
    @Transactional
    public String rejectUserFromGroup(
            @PathParam("groupId") String groupId,
            @PathParam("userId") String userId
    ) {
        return groupService.rejectFromEnteringGroup(
                groupId,
                token.getClaim("userId"),
                userId
        );
    }

    @GET
    @Path("/{groupId}/waiting/users")
    @ResponseStatus(200)
    public List<GroupUserDto> getWaitingUsers(
            @PathParam("groupId") String groupId
    ) {
        return groupService.getWaitingUsers(groupId, token.getClaim("userId"))
                .stream()
                .map(groupUserConverter::convertToModel)
                .toList();
    }

    @GET
    @Path("/joined")
    @ResponseStatus(200)
    public List<GroupDto> getJoinedGroups() {
        return groupService.getUserJoinedGroups(token.getClaim("userId"))
                .stream()
                .map(groupConverter::convertToModel)
                .toList();
    }

    @GET
    @Path("/{groupName}/search")
    @ResponseStatus(200)
    public List<GroupDto> getGroups(@PathParam("groupName") String groupName) {
        return groupService.getGroups(groupName)
                .stream()
                .map(groupConverter::convertToModel)
                .toList();
    }
}
