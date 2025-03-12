package org.chat.config;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.chat.exceptions.*;

@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable throwable) {
        if (throwable instanceof InvalidCredentialsException) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(throwable.getMessage())
                    .build();
        }

        if (throwable instanceof InvalidRoleException) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(throwable.getMessage())
                    .build();
        }

        if (throwable instanceof NotFoundException) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(throwable.getMessage())
                    .build();
        }

        if (
                throwable instanceof InvalidGroupException ||
                throwable instanceof InvalidInfoException
        ) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(throwable.getMessage())
                    .build();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Internal server error")
                .build();
    }
}
