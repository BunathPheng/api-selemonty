package org.hrd.finalprojectmuseum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
}
