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

    @GET
    @Path("/me")
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
        log.info("GET /groups/me called");

        var pageDto = groupService.getUserJoinedGroups(userContext.get().id(), page, size);
        var groups = pageDto.content()
                .stream()
                .map(groupConverter::convertToModel)
                .toList();

        log.info("GET /groups/me returning a {} of {}", PageDto.class.getName(), GroupDto.class.getName());

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
