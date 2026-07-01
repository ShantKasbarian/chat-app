package org.chat.controller;

import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ContactConverter;
import org.chat.model.ContactDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.ContactService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Path("/contacts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "SecurityScheme")
@Tag(name = "contacts", description = "contact management")
public class ContactController {
    private final ContactService contactService;

    private final ContactConverter contactConverter;

    private final UserContext userContext;

    @GET
    @Operation(summary = "get contacts", description = "returns the current users' contacts")
    @APIResponse(responseCode = "200", description = "contacts fetched successfully")
    public Response getContacts(
            @QueryParam("page")
            @DefaultValue("0")
            @Min(value = 0, message = "page must be at least 0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            @Min(value = 1, message = "size must be at least 1")
            int size
    ) {
        log.info("GET /contacts called with page {} and size {}", page, size);

        var query = contactService.findByUserId(userContext.get().id(), page, size);
        var contacts = query.list().stream()
                .map(contactConverter::convertToModel)
                .toList();
        PageDto<ContactDto> pageDto = new PageDto<>(contacts, page, size);

        log.info("GET /contacts returning a {} of {} with page {} and size {}", PageDto.class.getName(), ContactDto.class.getName(), page, size);

        return Response.ok(pageDto)
                .build();
    }

    @POST
    @Path("/users/{userId}")
    @Operation(summary = "create contact", description = "returns a new contact")
    @APIResponse(responseCode = "201", description = "contact has been created")
    @APIResponse(responseCode = "404", description = "target user not found")
    @APIResponse(responseCode = "409", description = "contact already exists")
    public Response addContact(@PathParam("userId") UUID userId) {
        log.info("POST /users/{} called", userId);

        var contact = contactConverter.convertToModel(
                contactService.addContact(userContext.get().id(), userId)
        );

        log.info("POST /users/{} is returning a {}", userId, ContactDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(contact)
                .build();
    }
}
