package org.chat.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.chat.converters.GroupConverter;
import org.chat.models.AcceptOrRejectToGroupDto;
import org.chat.models.GroupDto;
import org.chat.services.GroupService;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class GroupController {
    private final GroupService groupService;

    private final GroupConverter groupConverter;

    public GroupController(GroupService groupService, GroupConverter groupConverter) {
        this.groupService = groupService;
        this.groupConverter = groupConverter;
    }

    @POST
    @Path("/create")
    @ResponseStatus(201)
    @Transactional
    public String create(GroupDto groupDto) {
        return groupService.createGroup(
                groupConverter.convertToEntity(groupDto),
                groupDto.getCreators()
        );
    }

    @POST
    @Path("/join")
    @ResponseStatus(201)
    @Transactional
    public String joinGroup(GroupDto groupDto) {
        return groupService.joinGroup(groupDto.getName(), groupDto.getId());
    }

    @DELETE
    @Path("/leave")
    @ResponseStatus(204)
    @Transactional
    public String leaveGroup(GroupDto groupDto) {
        return groupService.leaveGroup(groupDto.getName(), groupDto.getId());
    }

    @PUT
    @Path("/accept/user")
    @ResponseStatus(200)
    @Transactional
    public String acceptUserToGroup(AcceptOrRejectToGroupDto acceptOrRejectToGroupDto) {
        return groupService.acceptToGroup(
                acceptOrRejectToGroupDto.getGroupName(),
                acceptOrRejectToGroupDto.getCurrentUserId(),
                acceptOrRejectToGroupDto.getUsername()
        );
    }

    @PUT
    @Path("/reject/user")
    @ResponseStatus(204)
    @Transactional
    public String rejectUserFromGroup(AcceptOrRejectToGroupDto acceptOrRejectToGroupDto) {
        return groupService.rejectFromEnteringGroup(
                acceptOrRejectToGroupDto.getGroupName(),
                acceptOrRejectToGroupDto.getCurrentUserId(),
                acceptOrRejectToGroupDto.getUsername()
        );
    }

    @GET
    @Path("/{groupName}/waiting/users")
    @ResponseStatus(200)
    public List<String> getWaitingUsers(
            @PathParam("groupName") String groupName,
            @QueryParam("userId") int userId
    ) {
        return groupService.getWaitingUsers(groupName, userId);
    }

    @GET
    @Path("/joined")
    @ResponseStatus(200)
    public List<String> getJoinedGroups(int currentUserId) {
        return groupService.getUserJoinedGroups(currentUserId);
    }
}
