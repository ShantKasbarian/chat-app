package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.UserConverter;
import org.chat.model.PageDto;
import org.chat.model.UserDto;
import org.chat.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Path("/users")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    private final UserService userService;

    private final UserConverter userConverter;

    @POST
    @Path("/login")
    public Response login(@Valid UserDto userDto) {
        String username = userDto.username();

        log.info("POST /auth/login is authenticating user {}", username);

        var tokenDto = userService.login(userDto.username(), userDto.password());

        log.info("POST /auth/login authenticated user {}", username);

        return Response.ok(tokenDto).build();
    }

    @POST
    @Path("/signup")
    public Response signup(@Valid UserDto userDto) {
        String username = userDto.username();

        log.info("POST/auth/signup is registering user {}", username);

        var tokenDto = userService.signup(username, userDto.password());

        log.info("POST/auth/signup is registered user {}", username);

        return Response.status(Response.Status.CREATED)
                .entity(tokenDto)
                .build();
    }

    @GET
    @Path("/{username}")
    public Response findByUsername(
            @PathParam("username") String username,
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size
    ) {
        log.info("GET /users/{username} is fetching users with size {} and page {}", size, page);

        var query = userService.findByUsername(username, page, size);
        var users = query.list()
                .stream()
                .map(userConverter::convertToModel)
                .toList();

        PageDto<UserDto> pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /users/{username} is returning users with size {} and page {}", size, page);

        return Response.ok(pageDto).build();
    }
}
