package org.chat.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.chat.model.ErrorMessageDto;

@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal server error";

    @Override
    public Response toResponse(Throwable throwable) {
        switch (throwable) {
            case InvalidCredentialsException e -> {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorMessageDto(e.getMessage()))
                        .build();
            }

            case InvalidRoleException e -> {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ErrorMessageDto(e.getMessage()))
                        .build();
            }

            case ResourceNotFoundException e -> {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorMessageDto(e.getMessage()))
                        .build();
            }

            case InvalidGroupException e -> {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorMessageDto(e.getMessage()))
                        .build();
            }

            case InvalidInfoException e -> {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorMessageDto(e.getMessage()))
                        .build();
            }

            case UnableToJoinGroupException e -> {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorMessageDto(e.getMessage()))
                        .build();
            }

            default -> {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorMessageDto(INTERNAL_SERVER_ERROR_MESSAGE))
                        .build();
            }
        }
    }
}
