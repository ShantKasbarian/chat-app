package org.chat.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.UserConverter;
import org.chat.model.*;
import org.chat.service.UserService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Slf4j
@RequiredArgsConstructor
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "users", description = "user management")
public class UserController {
    private final UserService userService;

    private final UserConverter userConverter;

    @POST
    @Path("/auth/login")
    @Operation(summary = "user authentication", description = "returns a jwt token")
    @APIResponse(
            responseCode = "200",
            description = "login successful",
            content = @Content(schema = @Schema(implementation = TokenDto.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "invalid values in request body",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @APIResponse(
            responseCode = "401",
            description = "wrong username or password",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    public Response login(@Valid LoginDto loginDto) {
        String username = loginDto.username();

        log.info("POST /users/auth/login is authenticating user {}", username);

        var tokenDto = userService.login(loginDto.username(), loginDto.password());

        log.info("POST /users/auth/login authenticated user {}", username);

        return Response.ok(tokenDto)
                .build();
    }

    @POST
    @Path("/auth/signup")
    @Operation(summary = "user registration", description = "returns a jwt token")
    @APIResponse(
            responseCode = "200",
            description = "registration successful",
            content = @Content(schema = @Schema(implementation = TokenDto.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "invalid values in request body",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @APIResponse(
            responseCode = "409",
            description = "user with given username already exists",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
    public Response signup(@Valid UserDto userDto) {
        String username = userDto.username();

        log.info("POST /users/auth/signup is registering user {}", username);

        var tokenDto = userService.signup(username, userDto.password());

        log.info("POST /users/auth/signup is registered user {}", username);

        return Response.status(Response.Status.CREATED)
                .entity(tokenDto)
                .build();
    }

    @GET
    @Path("/{username}")
    @SecurityRequirement(name = "SecurityScheme")
    @Operation(summary = "get users by username", description = "returns a list of users")
    @APIResponse(
            responseCode = "200",
            description = "users fetched successfully",
            content = @Content(schema = @Schema(implementation = PageDto.class))
    )
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
        log.info("GET /users/{} is fetching users with page {} and size {}", username, page, size);

        var query = userService.findByUsername(username, page, size);
        var users = query.list()
                .stream()
                .map(userConverter::convertToModel)
                .toList();

        PageDto<UserDto> pageDto = new PageDto<>(users, query.count(), query.pageCount());

        log.info("GET /users/{} is returning users with page {} and size {}", username, page, size);

        return Response.ok(pageDto)
                .build();
    }
}
