package org.chat.controller;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ToEntityConverter;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Group;
import org.chat.entity.GroupUser;
import org.chat.entity.User;
import org.chat.model.GroupDto;
import org.chat.model.GroupUserDto;
import org.chat.service.GroupService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;
import java.util.UUID;

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
    @Transactional
    public Response create(@Context JsonWebToken jsonWebToken, GroupDto groupDto) {
        log.info("/groups with POST called");

        var group = groupToModelConverter.convertToModel(
                groupService.createGroup(
                        groupDtoToEntityConverter.convertToEntity(groupDto),
                        groupDto.getCreators(),
                        UUID.fromString(jsonWebToken.getClaim(USER_ID_CLAIM))
                )
        );

        log.info("/groups with POST returning a {}", GroupDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(group)
                .build();
    }

    @POST
    @Path("/{groupId}/join")
    @ResponseStatus(201)
    @Transactional
    public Response joinGroup(@PathParam("groupId") UUID groupId) {
        log.info("/groups/{groupId}/join with POST called");

        var groupUserDto = groupUserToModelConverter.convertToModel(
                groupService.joinGroup(groupId, UUID.fromString(token.getClaim(USER_ID_CLAIM)))
        );

        log.info("/groups/{groupId}/join with POST returning a {}", GroupUserDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(groupUserDto)
                .build();
    }

    @DELETE
    @Path("/{groupId}/leave")
    @ResponseStatus(204)
    @Transactional
    public void leaveGroup(@PathParam("groupId") UUID groupId) {
        log.info("/groups/{groupId}/leave with DELETE called");

        groupService.leaveGroup(groupId, UUID.fromString(token.getClaim(USER_ID_CLAIM)));

        log.info("/groups/{groupId}/leave with DELETE user left group");
    }

    @PUT
    @Path("/accept/{groupUserId}")
    @Transactional
    public Response acceptUserToGroup(@PathParam("groupUserId") UUID groupUserId) {
        log.info("/groups/accept/{groupUserId} with PUT called");

        var groupUserDto = groupUserToModelConverter.convertToModel(
                groupService.acceptJoinGroup(
                    UUID.fromString(token.getClaim(USER_ID_CLAIM)), groupUserId
                )
        );

        log.info("/groups/accept/{groupUserId} with PUT returning a {}", GroupUserDto.class.getName());

        return Response.ok(groupUserDto).build();
    }

    @DELETE
    @Path("/reject/{groupUserId}")
    @ResponseStatus(204)
    @Transactional
    public void rejectUserFromGroup(@PathParam("groupUserId") UUID groupUserId) {
        log.info("/groups/reject/{groupUserId} with DELETE called");

        groupService.rejectJoinGroup(UUID.fromString(token.getClaim(USER_ID_CLAIM)), groupUserId);

        log.info("/groups/reject/{groupUserId} with DELETE returning a response");
    }

    @GET
    @Path("/{groupId}/waiting/users")
    public Response getWaitingUsers(@PathParam("groupId") UUID groupId) {
        log.info("/groups/{groupId}/waiting/users with GET called");

        var users = groupService.getWaitingUsers(groupId, UUID.fromString(token.getClaim(USER_ID_CLAIM)))
                .stream()
                .map(groupUserToModelConverter::convertToModel)
                .toList();

        log.info("/groups/{groupId}/waiting/users with GET returning a {} of {}", List.class.getName(), GroupUserDto.class);

        return Response.ok(users).build();
    }

    @GET
    @Path("/joined")
    public Response getJoinedGroups() {
        log.info("/groups/joined with GET called");

        var groups = groupService.getUserJoinedGroups(UUID.fromString(token.getClaim(USER_ID_CLAIM)))
                .stream()
                .map(groupToModelConverter::convertToModel)
                .toList();

        log.info("/groups/joined with GET returning a {} of {}", List.class.getName(), GroupDto.class.getName());

        return Response.ok(groups).build();
    }

    @GET
    @Path("/{groupName}")
    public Response getGroups(@PathParam("groupName") String groupName) {
        log.info("/groups/{groupName}/search with GET called");

        var groups = groupService.getGroups(groupName)
                .stream()
                .map(groupToModelConverter::convertToModel)
                .toList();

        log.info("/groups/{groupName}/search with GET returning a {} of {}", List.class.getName(), GroupDto.class.getName());

        return Response.ok(groups).build();
    }
}
