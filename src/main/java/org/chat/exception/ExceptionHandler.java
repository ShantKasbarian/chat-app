package org.chat.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.chat.model.ErrorMessageDto;

@Slf4j
@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {
  private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal server error";

  private static final String RESOURCE_NOT_FOUND_MESSAGE = "resource not found";

  @Override
  public Response toResponse(Throwable throwable) {
    Response.Status status;
    String message = throwable.getMessage();

    switch (throwable) {
      case InvalidCredentialsException e -> status = Response.Status.UNAUTHORIZED;
      case ResourceAlreadyExistsException e -> status = Response.Status.CONFLICT;
      case ResourceNotFoundException e -> status = Response.Status.NOT_FOUND;
      case ForbiddenException e -> status = Response.Status.FORBIDDEN;
      case NotFoundException e -> {
        status = Response.Status.NOT_FOUND;
        message = RESOURCE_NOT_FOUND_MESSAGE;
      }
      default -> {
        log.error(throwable.getMessage(), throwable);
        status = Response.Status.INTERNAL_SERVER_ERROR;
        message = INTERNAL_SERVER_ERROR_MESSAGE;
      }
    }

    return Response.status(status).entity(new ErrorMessageDto(message)).build();
  }
}
