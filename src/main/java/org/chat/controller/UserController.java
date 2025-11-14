package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.chat.converter.ContactConverter;
import org.chat.converter.UserConverter;
import org.chat.model.ContactDto;
import org.chat.model.UserDto;
import org.chat.service.impl.UserServiceImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

import static org.chat.config.JwtService.USER_ID_CLAIM;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    private final ContactConverter contactConverter;

    private final UserConverter userConverter;

    @POST
    @Path("/{userId}/add/contact")
    @ResponseStatus(201)
    @Transactional
    public ContactDto addContact(@PathParam("userId") String userId) {
        return contactConverter.convertToModel(
                userService.addContact(token.getClaim(USER_ID_CLAIM), userId)
        );
    }

    @GET
    @Path("/contacts")
    @ResponseStatus(200)
    public List<ContactDto> getContacts() {
        return userService.getContacts(token.getClaim(USER_ID_CLAIM))
                .stream()
                .map(contactConverter::convertToModel)
                .toList();
    }

    @GET
    @Path("/{username}/search")
    @ResponseStatus(200)
    public List<UserDto> searchUserByUsername(@PathParam("username") String username) {
        return userService.searchUserByUsername(username)
                .stream().map(userConverter::convertToModel)
                .toList();
    }
}
