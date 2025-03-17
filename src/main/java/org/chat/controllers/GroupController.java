package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.converters.GroupConverter;
import org.chat.models.GroupDto;
import org.chat.services.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class GroupController {
    private final GroupService groupService;

    private final GroupConverter groupConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    public GroupController(
            GroupService groupService,
            GroupConverter groupConverter,
            SecurityContext securityContext,
            JsonWebToken token
    ) {
        this.groupService = groupService;
        this.groupConverter = groupConverter;
        this.securityContext = securityContext;
        this.token = token;
    }

    @POST
    @Path("/create")
    @ResponseStatus(201)
    @Transactional
    public GroupDto create(GroupDto groupDto) {
        String userId = token.getClaim("userId");

        return groupConverter.convertToModel(
                groupService.createGroup(
                    groupConverter.convertToEntity(groupDto),
                    groupDto.getCreators(),
                    Long.valueOf(userId)
                )
        );
    }

    @POST
    @Path("/{groupName}/join")
    @ResponseStatus(201)
    @Transactional
    public String joinGroup(@PathParam("groupName") String groupName) {
        String userId = token.getClaim("userId");
        return groupService.joinGroup(groupName, Long.valueOf(userId));
    }

    @DELETE
    @Path("/{groupName}/leave")
    @ResponseStatus(204)
    @Transactional
    public String leaveGroup(@PathParam("groupName") String groupName) {
        String userId = token.getClaim("userId");
        return groupService.leaveGroup(groupName, Long.valueOf(userId));
    }

    @PUT
    @Path("/{groupName}/accept/user/{username}")
    @ResponseStatus(200)
    @Transactional
    public String acceptUserToGroup(
            @PathParam("groupName") String groupName,
            @PathParam("username") String username
    ) {
        String userId = token.getClaim("userId");

        return groupService.acceptToGroup(
                groupName,
                Long.valueOf(userId),
                username
        );
    }

    @DELETE
    @Path("/{groupName}/reject/user/{username}")
    @ResponseStatus(204)
    @Transactional
    public String rejectUserFromGroup(
            @PathParam("groupName") String groupName,
            @PathParam("username") String username
    ) {
        String userId = token.getClaim("userId");

        return groupService.rejectFromEnteringGroup(
                groupName,
                Long.valueOf(userId),
                username
        );
    }

    @GET
    @Path("/{groupName}/waiting/users")
    @ResponseStatus(200)
    public List<String> getWaitingUsers(
            @PathParam("groupName") String groupName
    ) {
        String userId = token.getClaim("userId");

        return groupService.getWaitingUsers(groupName, Long.valueOf(userId));
    }

    @GET
    @Path("/joined")
    @ResponseStatus(200)
    public List<String> getJoinedGroups() {
        String userId = token.getClaim("userId");
        return groupService.getUserJoinedGroups(Long.valueOf(userId));
    }

    @GET
    @Path("/{groupName}/search")
    @ResponseStatus(200)
    public List<String> getGroups(@PathParam("groupName") String groupName) {
        return groupService.getGroups(groupName);
    }
}
