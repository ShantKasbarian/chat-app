package org.chat.config;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.chat.exceptions.*;

@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable throwable) {
        switch (throwable) {
            case InvalidCredentialsException e -> {
                return Response
                        .status(Response.Status.UNAUTHORIZED)
                        .entity(e.getMessage())
                        .build();
            }

            case InvalidRoleException e -> {
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity(e.getMessage())
                        .build();
            }

            case ResourceNotFoundException e -> {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(e.getMessage())
                        .build();
            }

            case InvalidGroupException e -> {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(e.getMessage())
                        .build();
            }

            case InvalidInfoException e -> {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(e.getMessage())
                        .build();
            }

            default -> {
                return Response
                        .status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("internal server error")
                        .build();
            }

        }
    }
}
