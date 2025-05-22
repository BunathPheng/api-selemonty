package org.hrd.finalprojectmuseum.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(AppNotFoundException.class)
    public ProblemDetail handleException(AppNotFoundException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail(e.getMessage());
        detail.setProperty("timestamp", LocalDateTime.now());

        return detail;
    }

    @ExceptionHandler(ThrowFieldException.class)
    public ProblemDetail handleThrowFieldException(ThrowFieldException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put(e.getField(), e.getMessage());

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());
        detail.setDetail("BAD REQUEST");
        detail.setProperty("errors", errors);

        return detail;
    }

    @ExceptionHandler(AppBadRequestException.class)
    public ProblemDetail handleBadRequestException(AppBadRequestException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(e.getMessage());
        detail.setProperty("timestamp", LocalDateTime.now());
        return detail;
    }

    @ExceptionHandler(InvalidOptException.class)
    public ProblemDetail handleInvalidOptException(InvalidOptException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(e.getMessage());
        detail.setProperty("timestamp", LocalDateTime.now());
        return detail;
    }

    // Handle UUID type mismatch in path variables and request parameters
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());

        if (e.getRequiredType() != null && e.getRequiredType().equals(UUID.class)) {
            detail.setDetail("Invalid UUID format for parameter: " + e.getName());
            Map<String, String> errors = new HashMap<>();
            errors.put(e.getName(), "Invalid UUID format. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            detail.setProperty("errors", errors);
        } else {
            detail.setDetail("Invalid parameter type for: " + e.getName());
            Map<String, String> errors = new HashMap<>();
            errors.put(e.getName(), "Invalid parameter type. Expected: " +
                    (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "Unknown"));
            detail.setProperty("errors", errors);
        }

        return detail;
    }


    // Handle UUID format errors in request body (JSON deserialization)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());

        // Check if it's a UUID format error
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) e.getCause();
            if (ife.getTargetType() != null && ife.getTargetType().equals(UUID.class)) {
                String fieldName = "unknown";
                if (!ife.getPath().isEmpty()) {
                    fieldName = ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                }

                detail.setDetail("Invalid UUID format in request body");
                Map<String, String> errors = new HashMap<>();
                errors.put(fieldName, "Invalid UUID format. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                detail.setProperty("errors", errors);
                return detail;
            }
        }

        // Handle other JSON parsing errors
        detail.setDetail("Invalid JSON format or malformed request body");
        return detail;
    }

    // Handle general IllegalArgumentException (includes UUID.fromString errors)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());

        // Check if it's a UUID parsing error
        if (e.getMessage() != null && e.getMessage().contains("Invalid UUID string")) {
            detail.setDetail("Invalid UUID format provided");
            Map<String, String> errors = new HashMap<>();
            errors.put("uuid", "Invalid UUID format. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            detail.setProperty("errors", errors);
        } else {
            detail.setDetail("Invalid input: " + e.getMessage());
        }

        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());

        // Field errors (field-level validations)
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Global errors (class-level validations)
        for (org.springframework.validation.ObjectError error : e.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }

        detail.setDetail("BAD REQUEST");
        detail.setProperty("errors", errors);

        return detail;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handlerMethodValidationException(HandlerMethodValidationException e) {
        List<String> errors = new ArrayList<>();
        for (MessageSourceResolvable pathError : e.getAllErrors()) {
            errors.add(pathError.getDefaultMessage());
        }
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail("BAD REQUEST");
        detail.setProperty("timestamp", LocalDateTime.now());
        detail.setProperty("errors", errors);
        return detail;
    }
}
