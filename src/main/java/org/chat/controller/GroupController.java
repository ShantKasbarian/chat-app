package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ToEntityConverter;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
import org.chat.service.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

import static org.chat.config.JwtService.USER_ID_CLAIM;

@Slf4j
@RequiredArgsConstructor
@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class GroupController {
    private final GroupService groupService;

    private final ToModelConverter<GroupDto, Group> groupToModelConverter;

    private final ToEntityConverter<Group, GroupDto> groupDtoToEntityConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    private final ToModelConverter<GroupUserDto, GroupUser> groupUserToModelConverter;

    @POST
    @ResponseStatus(201)
    @Transactional
    public GroupDto create(GroupDto groupDto) {
        log.info("/groups with POST called");

        var response = groupToModelConverter.convertToModel(
                groupService.createGroup(
                    groupDtoToEntityConverter.convertToEntity(groupDto),
                    groupDto.getCreators(),
                    token.getClaim(USER_ID_CLAIM)
                )
        );

        log.info("/groups with POST returning a {}", GroupDto.class.getName());

        return response;
    }

    @POST
    @Path("/{groupId}/join")
    @ResponseStatus(201)
    @Transactional
    public GroupUserDto joinGroup(@PathParam("groupId") String groupId) {
        log.info("/groups/{groupId}/join with POST called");

        var response = groupUserToModelConverter.convertToModel(
                groupService.joinGroup(groupId, token.getClaim(USER_ID_CLAIM))
        );

        log.info("/groups/{groupId}/join with POST returning a {}", GroupUserDto.class.getName());

        return response;
    }

    @DELETE
    @Path("/{groupId}/leave")
    @ResponseStatus(204)
    @Transactional
    public String leaveGroup(@PathParam("groupId") String groupId) {
        log.info("/groups/{groupId}/leave with DELETE called");

        var response = groupService.leaveGroup(groupId, token.getClaim(USER_ID_CLAIM));

        log.info("/groups/{groupId}/leave with DELETE user left group");

        return response;
    }

    @PUT
    @Path("/{groupId}/accept/user/{userId}")
    @ResponseStatus(200)
    @Transactional
    public GroupUserDto acceptUserToGroup(
            @PathParam("groupId") String groupId,
            @PathParam("userId") String userId
    ) {
        log.info("/groups/{groupId}/accept/user/{userId} with PUT called");

        var response = groupUserToModelConverter.convertToModel(
                groupService.acceptToGroup(
                    groupId,
                    token.getClaim(USER_ID_CLAIM),
                    userId
                )
        );

        log.info("/groups/{groupId}/accept/user/{userId} with PUT returning a {}", GroupUserDto.class.getName());

        return response;
    }

    @DELETE
    @Path("/{groupId}/reject/user/{userId}")
    @ResponseStatus(204)
    @Transactional
    public String rejectUserFromGroup(
            @PathParam("groupId") String groupId,
            @PathParam("userId") String userId
    ) {
        log.info("/groups/{groupId}/reject/user/{userId} with DELETE called");

        var response = groupService.rejectFromEnteringGroup(
                groupId, token.getClaim(USER_ID_CLAIM), userId
        );

        log.info("/groups/{groupId}/reject/user/{userId} with DELETE returning a response");

        return response;
    }

    @GET
    @Path("/{groupId}/waiting/users")
    @ResponseStatus(200)
    public List<GroupUserDto> getWaitingUsers(@PathParam("groupId") String groupId) {
        log.info("/groups/{groupId}/waiting/users with GET called");

        var response = groupService.getWaitingUsers(groupId, token.getClaim(USER_ID_CLAIM))
                .stream()
                .map(groupUserToModelConverter::convertToModel)
                .toList();

        log.info("/groups/{groupId}/waiting/users with GET returning a {} of {}", List.class.getName(), GroupUserDto.class);

        return response;
    }

    @GET
    @Path("/joined")
    @ResponseStatus(200)
    public List<GroupDto> getJoinedGroups() {
        log.info("/groups/joined with GET called");

        var response = groupService.getUserJoinedGroups(token.getClaim(USER_ID_CLAIM))
                .stream()
                .map(groupToModelConverter::convertToModel)
                .toList();

        log.info("/groups/joined with GET returning a {} of {}", List.class.getName(), GroupDto.class.getName());

        return response;
    }

    @GET
    @Path("/{groupName}/search")
    @ResponseStatus(200)
    public List<GroupDto> getGroups(@PathParam("groupName") String groupName) {
        log.info("/groups/{groupName}/search with GET called");

        var response = groupService.getGroups(groupName)
                .stream()
                .map(groupToModelConverter::convertToModel)
                .toList();

        log.info("/groups/{groupName}/search with GET returning a {} of {}", List.class.getName(), GroupDto.class.getName());

        return response;
    }
}
