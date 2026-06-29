package org.chat.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.chat.model.ErrorResponseDto;
import org.chat.model.FieldViolationDto;

import java.util.List;
import java.util.stream.StreamSupport;

@Provider
public class ConstraintValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {
    private static final String CONSTRAINT_VIOLATIONS_TITLE = "Constraint Violation";

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<FieldViolationDto> violations = exception.getConstraintViolations()
            .stream()
            .map(v -> new FieldViolationDto(
                StreamSupport.stream(v.getPropertyPath().spliterator(), false)
                    .reduce((a, b) -> b)
                    .map(Object::toString)
                    .orElse(""),
                v.getMessage()
            ))
            .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponseDto(CONSTRAINT_VIOLATIONS_TITLE, violations))
                .build();
    }
}
