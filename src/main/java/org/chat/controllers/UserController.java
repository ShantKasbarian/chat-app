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
        String userId = token.getClaim("userId");
        Contact contact = userService.addContact(Long.valueOf(userId), username);
        return new ContactDto(contact.id, contact.getContact().id, contact.getContact().getUsername());
    }

    @GET
    @Path("/contacts")
    @ResponseStatus(200)
    public List<String> getContacts() {
        String userId = token.getClaim("userId");
        return userService.getContacts(Long.valueOf(userId));
    }

    @GET
    @Path("/{username}/search")
    @ResponseStatus(200)
    public List<String> searchUserByUsername(@PathParam("username") String username) {
        return userService.searchUserByUsername(username);
    }
}
