package org.chat.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.chat.models.UserDto;
import org.chat.services.UserService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
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
    @Path("/add/contact")
    public String addContact(UserDto userDto) {
        return userService.addContact(userDto.getId(), userDto.getUsername());
    }

    @GET
    @Path("/contacts")
    public List<String> getContacts(int id) {
        return userService.getContacts(id);
    }
}
