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
import org.chat.converter.ContactConverter;
import org.chat.converter.UserConverter;
import org.chat.model.ContactDto;
import org.chat.model.UserDto;
import org.chat.service.impl.UserServiceImpl;
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
    private final UserServiceImpl userService;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    private final ContactConverter contactConverter;

    private final UserConverter userConverter;

    @POST
    @Path("/{userId}/contact")
    @Transactional
    public Response addContact(@PathParam("userId") UUID userId) {
        log.info("/users/{userId}/contact with POST called");

        var contact = contactConverter.convertToModel(
                userService.addContact(UUID.fromString(token.getClaim(USER_ID_CLAIM)), userId)
        );

        log.info("/users/{userId}/contact with POST returning a {}", ContactDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(contact)
                .build();
    }

    @GET
    @Path("/contacts")
    public Response getContacts() {
        log.info("/users/contacts with GET called");

        var contacts = userService.getContacts(UUID.fromString(token.getClaim(USER_ID_CLAIM)))
                .stream()
                .map(contactConverter::convertToModel)
                .toList();

        log.info("/users/contacts returning a {} of {}", List.class.getName(), ContactDto.class.getName());

        return Response.ok(contacts).build();
    }

    @GET
    @Path("/{username}")
    public Response searchUserByUsername(@PathParam("username") String username) {
        log.info("/users/{username} with GET called");

        var users = userService.searchUserByUsername(username)
                .stream().map(userConverter::convertToModel)
                .toList();

        log.info("/users/{username} with GET returning a {} of {}", List.class.getName(), UserDto.class.getName());

        return Response.ok(users).build();
    }
}
