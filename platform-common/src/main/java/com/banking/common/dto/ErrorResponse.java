package com.banking.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String code;       // e.g., "NOT_FOUND", "CONFLICT", "VALIDATION_ERROR"
    private String message;    // human-friendly error detail
    private Instant timestamp; // server timestamp
    private String path;       // request URI
}
