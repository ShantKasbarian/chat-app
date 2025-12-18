package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Contact;
import org.chat.entity.User;
import org.chat.model.ContactDto;
import org.chat.model.UserDto;
import org.chat.service.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

import static org.chat.config.JwtService.USER_ID_CLAIM;

@Slf4j
@RequiredArgsConstructor
@Path("/users")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    private final UserService userService;

    private final ToModelConverter<UserDto, User> userToModelConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    @GET
    @Path("/{username}")
    public Response searchUserByUsername(@PathParam("username") String username) {
        log.info("/users/{username} with GET called");

        var users = userService.searchUserByUsername(username)
                .stream().map(userToModelConverter::convertToModel)
                .toList();

        log.info("/users/{username} with GET returning a {} of {}", List.class.getName(), UserDto.class.getName());

        return Response.ok(users).build();
    }
}
