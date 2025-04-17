package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.chat.converters.ContactConverter;
import org.chat.converters.UserConverter;
import org.chat.entities.Contact;
import org.chat.models.ContactDto;
import org.chat.models.UserDto;
import org.chat.services.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
                userService.addContact(token.getClaim("userId"), userId)
        );
    }

    @GET
    @Path("/contacts")
    @ResponseStatus(200)
    public List<ContactDto> getContacts() {
        return userService.getContacts(token.getClaim("userId"))
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
