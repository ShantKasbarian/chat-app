package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ContactConverter;
import org.chat.converter.UserConverter;
import org.chat.model.ContactDto;
import org.chat.model.UserDto;
import org.chat.service.impl.UserServiceImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

import static org.chat.config.JwtService.USER_ID_CLAIM;

@Slf4j
@RequiredArgsConstructor
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class UserController {
    private final UserServiceImpl userService;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    private final ContactConverter contactConverter;

    private final UserConverter userConverter;

    @POST
    @Path("/{userId}/contact")
    @ResponseStatus(201)
    @Transactional
    public ContactDto addContact(@PathParam("userId") String userId) {
        log.info("/users/{userId}/contact with POST called");

        var contact = contactConverter.convertToModel(
                userService.addContact(token.getClaim(USER_ID_CLAIM), userId)
        );

        log.info("/users/{userId}/contact with POST returning a {}", ContactDto.class.getName());

        return contact;
    }

    @GET
    @Path("/contacts")
    @ResponseStatus(200)
    public List<ContactDto> getContacts() {
        log.info("/users/contacts with GET called");

        var contacts = userService.getContacts(token.getClaim(USER_ID_CLAIM))
                .stream()
                .map(contactConverter::convertToModel)
                .toList();

        log.info("/users/contacts returning a {} of {}", List.class.getName(), ContactDto.class.getName());

        return contacts;
    }

    @GET
    @Path("/{username}")
    @ResponseStatus(200)
    public List<UserDto> searchUserByUsername(@PathParam("username") String username) {
        log.info("/users/{username} with GET called");

        var users = userService.searchUserByUsername(username)
                .stream().map(userConverter::convertToModel)
                .toList();

        log.info("/users/{username} with GET returning a {} of {}", List.class.getName(), UserDto.class.getName());

        return users;
    }
}
