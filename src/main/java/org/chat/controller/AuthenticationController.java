package org.chat.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
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
import org.chat.service.UserService;

@Slf4j
@Path("/auth")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @POST
    @Path("/login")
    public Response login(@Valid UserDto userDto) {
        log.info("POST /auth/login called");

        var tokenDto = userService.login(userDto.username(), userDto.password());

        log.info("POST /auth/login is returning a {}", TokenDto.class.getName());

        return Response.ok(tokenDto).build();
    }

    @POST
    @Path("/signup")
    public Response signup(@Valid UserDto userDto) {
        log.info("POST/auth/signup called");

        var tokenDto = userService.signUp(userDto.username(), userDto.password());

        log.info("POST /auth/signup is returning a {}", TokenDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(tokenDto)
                .build();
    }
}
