package org.chat.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;
import org.chat.model.ErrorMessageDto;

@Provider
public class ConstraintValidationExceptionHandler
    implements ExceptionMapper<ConstraintViolationException> {
  @Override
  public Response toResponse(ConstraintViolationException exception) {
    String message =
        exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining("\n"));

    return Response.status(Response.Status.BAD_REQUEST)
        .entity(new ErrorMessageDto(message))
        .build();
  }
}
