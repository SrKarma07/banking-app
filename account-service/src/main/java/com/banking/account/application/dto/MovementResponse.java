package com.banking.account.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovementResponse {
    private Long id;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;
    private Instant date;
}
