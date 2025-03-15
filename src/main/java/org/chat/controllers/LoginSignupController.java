package org.chat.controllers;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.chat.models.UserDto;
import org.chat.services.LoginSignupService;

import java.net.URI;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginSignupController {
    private final LoginSignupService loginSignupService;

    public LoginSignupController(LoginSignupService loginSignupService) {
        this.loginSignupService = loginSignupService;
    }

    @Path("/login")
    @PermitAll
    @POST
    public Response login(UserDto userDto) {
        return Response.ok(
                loginSignupService.login(userDto.getUsername(), userDto.getPassword())
        ).build();
    }

    @Path("/signup")
    @PermitAll
    @POST
    public Response signup(UserDto userDto) {
        return Response
                .created(URI.create("/login"))
                .entity(
                        loginSignupService.createUser(
                                userDto.getUsername(),
                                userDto.getPassword()
                        )
                ).build();
    }
}
