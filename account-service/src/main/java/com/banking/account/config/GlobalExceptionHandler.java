package com.banking.account.config;

import com.banking.common.dto.ErrorResponse;
import com.banking.common.exception.ConflictException;
import com.banking.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("NotFound: {}", ex.getMessage());
        return build("NOT_FOUND", ex.getMessage(), req);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException ex, HttpServletRequest req) {
        log.warn("Conflict: {}", ex.getMessage());
        return build("CONFLICT", ex.getMessage(), req);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(Exception ex, HttpServletRequest req) {
        log.warn("Validation error: {}", ex.getMessage());
        return build("VALIDATION_ERROR", "Invalid request payload", req);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        return build("INTERNAL_ERROR", "Unexpected error", req);
    }

    private ErrorResponse build(String code, String msg, HttpServletRequest req) {
        return ErrorResponse.builder()
                .code(code)
                .message(msg)
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }
}
