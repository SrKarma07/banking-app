package com.banking.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovementRequest {

    @NotBlank @Pattern(regexp = "DEBIT|CREDIT")
    private String type;

    @NotNull @Positive
    private BigDecimal amount;
}
