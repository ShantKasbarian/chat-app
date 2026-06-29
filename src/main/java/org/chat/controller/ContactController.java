package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ContactConverter;
import org.chat.model.ContactDto;
import org.chat.model.PageDto;
import org.chat.security.UserContext;
import org.chat.service.ContactService;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Path("/contacts")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContactController {
    private final ContactService contactService;

    private final ContactConverter contactConverter;

    private final UserContext userContext;

    @GET
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
        log.info("GET /contacts called");

        var query = contactService.findByUserId(userContext.get().id(), page, size);
        var contacts = query.list().stream()
                .map(contactConverter::convertToModel)
                .toList();
        PageDto<ContactDto> pageDto = new PageDto<>(contacts, page, size);

        log.info("GET /contacts returning a {} of {}", PageDto.class.getName(), ContactDto.class.getName());

        return Response.ok(pageDto).build();
    }

    @POST
    @Path("/users/{userId}")
    public Response addContact(@PathParam("userId") UUID userId) {
        log.info("POST /users/{userId} called");

        var contact = contactConverter.convertToModel(
                contactService.addContact(userContext.get().id(), userId)
        );

        log.info("POST /users/{userId} is returning a {}", ContactDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(contact)
                .build();
    }
}
