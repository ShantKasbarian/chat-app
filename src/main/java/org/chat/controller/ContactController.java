package org.chat.controller;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.converter.ToModelConverter;
import org.chat.entity.Contact;
import org.chat.model.ContactDto;
import org.chat.model.PageDto;
import org.chat.service.ContactService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

import static org.chat.service.impl.JwtServiceImpl.USER_ID_CLAIM;

@Slf4j
@AllArgsConstructor
@Path("/contacts")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContactController {
    private final ContactService contactService;

    private final ToModelConverter<ContactDto, Contact> contactToModelConverter;

    @Context
    private final SecurityContext securityContext;

    private final JsonWebToken token;

    @GET
    public Response getContacts(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        log.info("/users/contacts with GET called");

        var query = contactService.findByUserId(UUID.fromString(token.getClaim(USER_ID_CLAIM)), page, size);
        var contacts = query.list().stream()
                .map(contactToModelConverter::convertToModel)
                .toList();
        PageDto<ContactDto> pageDto = new PageDto<>(contacts, page, size);

        log.info("/users/contacts returning a {} of {}", List.class.getName(), ContactDto.class.getName());

        return Response.ok(pageDto).build();
    }

    @POST
    @Path("/users/{userId}")
    @Transactional
    public Response addContact(@PathParam("userId") UUID userId) {
        log.info("POST /users/{userId}/contact called");

        var contact = contactToModelConverter.convertToModel(
                contactService.addContact(UUID.fromString(token.getClaim(USER_ID_CLAIM)), userId)
        );

        log.info("POST /users/{userId}/contact is returning a {}", ContactDto.class.getName());

        return Response.status(Response.Status.CREATED)
                .entity(contact)
                .build();
    }
}
