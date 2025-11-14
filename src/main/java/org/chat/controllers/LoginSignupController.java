package org.chat.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.chat.models.UserDto;
import org.chat.services.LoginSignupService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class LoginSignupController {
    private final LoginSignupService loginSignupService;

    @Path("/login")
    @PermitAll
    @POST
    public Response login(UserDto userDto) {
        return Response.ok(loginSignupService.login(userDto.username(), userDto.password()))
                .build();
    }

    @Path("/signup")
    @PermitAll
    @POST
    public Response signup(UserDto userDto) {
        return Response.status(Response.Status.CREATED)
                .entity(loginSignupService.createUser(userDto.username(), userDto.password()))
                .build();
    }
}
