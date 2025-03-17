package org.chat.controllers;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.entities.Contact;
import org.chat.models.ContactDto;
import org.chat.services.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.util.List;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class UserController {
    private final UserService userService;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    public UserController(
            UserService userService,
            SecurityContext securityContext,
            JsonWebToken token
    ) {
        this.userService = userService;
        this.securityContext = securityContext;
        this.token = token;
    }

    @POST
    @Path("/{username}/add/contact")
    @ResponseStatus(201)
    @Transactional
    public ContactDto addContact(@PathParam("username") String username) {
        Contact contact = userService.addContact(token.getClaim("userId"), username);
        return new ContactDto(contact.getId(), contact.getContact().getId(), contact.getContact().getUsername());
    }

    @GET
    @Path("/contacts")
    @ResponseStatus(200)
    public List<String> getContacts() {
        return userService.getContacts(token.getClaim("userId"));
    }

    @GET
    @Path("/{username}/search")
    @ResponseStatus(200)
    public List<String> searchUserByUsername(@PathParam("username") String username) {
        return userService.searchUserByUsername(username);
    }
}
