package org.chat.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.model.TokenDto;
import org.chat.model.UserDto;
import org.chat.service.AuthenticationService;

@Slf4j
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Path("/login")
    @PermitAll
    @POST
    public Response login(UserDto userDto) {
        log.info("/auth/login with POST called");
        var response = Response.ok(authenticationService.login(userDto.username(), userDto.password()))
                .build();

        log.info("/auth/login with POST is returning a {}", TokenDto.class.getName());

        return response;
    }

    @Path("/signup")
    @PermitAll
    @POST
    public Response signup(UserDto userDto) {
        log.info("/auth/signup with POST called");

        var response = Response.status(Response.Status.CREATED)
                .entity(authenticationService.createUser(userDto.username(), userDto.password()))
                .build();

        log.info("/auth/signup with POST is returning a {}", TokenDto.class.getName());

        return response;
    }
}
