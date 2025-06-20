package org.hrd.finalprojectmuseum.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;
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
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(AppNotFoundException.class)
    public ProblemDetail handleException(AppNotFoundException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail("RESOURCE NOT FOUND");

        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        detail.setProperty("errors", errors);

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
        detail.setDetail("BAD REQUEST"); // general message in detail

        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage()); // put detailed message here
        detail.setProperty("errors", errors);

        detail.setProperty("timestamp", LocalDateTime.now());
        return detail;
    }


    @ExceptionHandler(InvalidOptException.class)
    public ProblemDetail handleInvalidOptException(InvalidOptException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail("BAD REQUEST");

        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());
        detail.setProperty("errors", errors);

        detail.setProperty("timestamp", LocalDateTime.now());
        return detail;
    }

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


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());

        // Log the actual error for debugging
        System.err.println("JSON parsing error: " + e.getMessage());
        if (e.getCause() != null) {
            System.err.println("Root cause: " + e.getCause().getMessage());
            e.getCause().printStackTrace();
        }

        // Check if it's an InvalidFormatException
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) e.getCause();

            // Handle UUID format errors
            if (ife.getTargetType() != null && ife.getTargetType().equals(UUID.class)) {
                String fieldName = getFieldName(ife);
                detail.setDetail("Invalid UUID format in request body");
                Map<String, String> errors = new HashMap<>();
                errors.put(fieldName, "Invalid UUID format. Expected format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                detail.setProperty("errors", errors);
                return detail;
            }

            // Handle your custom DayOfWeek enum errors
            if (ife.getTargetType() != null && ife.getTargetType().equals(org.hrd.finalprojectmuseum.model.enums.DayOfWeek.class)) {
                String fieldName = getFieldName(ife);
                detail.setDetail("Invalid day of week in request body");
                Map<String, String> errors = new HashMap<>();
                errors.put(fieldName, "Invalid day of week. Valid values: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday (case insensitive)");
                detail.setProperty("errors", errors);
                return detail;
            }

            // Handle LocalTime format errors
            if (ife.getTargetType() != null && ife.getTargetType().equals(LocalTime.class)) {
                String fieldName = getFieldName(ife);
                detail.setDetail("Invalid time format in request body");
                Map<String, String> errors = new HashMap<>();
                errors.put(fieldName, "Invalid time format. Expected format: HH:mm (e.g., 09:30, 14:00)");
                detail.setProperty("errors", errors);
                return detail;
            }
            // Add this to your handleHttpMessageNotReadableException method
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String fieldName = getFieldName(ife);
                detail.setDetail("Invalid enum value in request body");
                Map<String, String> errors = new HashMap<>();

                // Get valid enum values
                Object[] enumConstants = ife.getTargetType().getEnumConstants();
                String validValues = Arrays.stream(enumConstants)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                errors.put(fieldName, "Invalid value. Valid values: " + validValues);
                detail.setProperty("errors", errors);
                return detail;
            }

            // Handle other format errors
            String fieldName = getFieldName(ife);
            detail.setDetail("Invalid format for field: " + fieldName);
            Map<String, String> errors = new HashMap<>();
            errors.put(fieldName, "Invalid value format for type: " + ife.getTargetType().getSimpleName());
            detail.setProperty("errors", errors);
            return detail;
        }

        // Check if it's a JsonParseException (malformed JSON)
        if (e.getCause() instanceof com.fasterxml.jackson.core.JsonParseException) {
            detail.setDetail("Malformed JSON: " + e.getCause().getMessage());
            return detail;
        }

        // Check if it's a JsonMappingException
        if (e.getCause() instanceof com.fasterxml.jackson.databind.JsonMappingException) {
            com.fasterxml.jackson.databind.JsonMappingException jme = (com.fasterxml.jackson.databind.JsonMappingException) e.getCause();
            detail.setDetail("JSON mapping error: " + jme.getOriginalMessage());
            return detail;
        }


        // Generic JSON error
        detail.setDetail("Invalid JSON format or malformed request body: " + e.getMessage());
        return detail;
    }

    private String getFieldName(InvalidFormatException ife) {
        if (!ife.getPath().isEmpty()) {
            return ife.getPath().get(ife.getPath().size() - 1).getFieldName();
        }
        return "unknown";
    }

    // Handle general IllegalArgumentException (includes UUID.fromString errors)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setProperty("timestamp", LocalDateTime.now());

        Map<String, String> errors = new HashMap<>();
        if (e.getMessage() != null && e.getMessage().contains("Invalid UUID string")) {
            detail.setDetail("Invalid UUID format provided");
            errors.put("uuid", "Invalid UUID format. Expected: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        } else {
            detail.setDetail("Invalid input");
            errors.put("error", e.getMessage());
        }

        detail.setProperty("errors", errors);
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
