package com.banking.customer.config;

import com.banking.common.dto.ErrorResponse;
import com.banking.common.exception.ConflictException;
import com.banking.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;          // << IMPORTANTE
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j                                     // << Asegúrate de que está
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("NotFound: {}", ex.getMessage());    // ← ya compila
        return ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException ex, HttpServletRequest req) {
        log.warn("Conflict: {}", ex.getMessage());
        return ErrorResponse.builder()
                .code("CONFLICT")
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(Exception ex, HttpServletRequest req) {
        log.warn("Validation error: {}", ex.getMessage());
        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Invalid request payload")
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        return ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Unexpected error")
                .path(req.getRequestURI())
                .timestamp(Instant.now())
                .build();
    }
}
