package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.converters.GroupConverter;
import org.chat.models.AcceptOrRejectToGroupDto;
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
    public String create(GroupDto groupDto) {
        String userId = token.getClaim("userId");

        return groupService.createGroup(
                groupConverter.convertToEntity(groupDto),
                groupDto.getCreators(),
                userId
        );
    }

    @POST
    @Path("/{groupName}/join")
    @ResponseStatus(201)
    @Transactional
    public String joinGroup(@PathParam("groupName") String groupName) {
        String userId = token.getClaim("userId");
        return groupService.joinGroup(groupName, Integer.parseInt(userId));
    }

    @DELETE
    @Path("/{groupName}/leave")
    @ResponseStatus(204)
    @Transactional
    public String leaveGroup(@PathParam("groupName") String groupName) {
        String userId = token.getClaim("userId");
        return groupService.leaveGroup(groupName, Integer.parseInt(userId));
    }

    @PUT
    @Path("/accept/user")
    @ResponseStatus(200)
    @Transactional
    public String acceptUserToGroup(AcceptOrRejectToGroupDto acceptOrRejectToGroupDto) {
        String userId = token.getClaim("userId");

        return groupService.acceptToGroup(
                acceptOrRejectToGroupDto.getGroupName(),
                Integer.parseInt(userId),
                acceptOrRejectToGroupDto.getUsername()
        );
    }

    @DELETE
    @Path("/reject/user")
    @ResponseStatus(204)
    @Transactional
    public String rejectUserFromGroup(AcceptOrRejectToGroupDto acceptOrRejectToGroupDto) {
        String userId = token.getClaim("userId");

        return groupService.rejectFromEnteringGroup(
                acceptOrRejectToGroupDto.getGroupName(),
                Integer.parseInt(userId),
                acceptOrRejectToGroupDto.getUsername()
        );
    }

    @GET
    @Path("/{groupName}/waiting/users")
    @ResponseStatus(200)
    public List<String> getWaitingUsers(
            @PathParam("groupName") String groupName
    ) {
        String userId = token.getClaim("userId");

        return groupService.getWaitingUsers(groupName, Integer.parseInt(userId));
    }

    @GET
    @Path("/joined")
    @ResponseStatus(200)
    public List<String> getJoinedGroups() {
        String userId = token.getClaim("userId");
        return groupService.getUserJoinedGroups(Integer.parseInt(userId));
    }
}
