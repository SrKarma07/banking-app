package com.banking.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountCreateRequest {

    @NotBlank @Size(max = 20)
    private String number;

    @NotBlank @Pattern(regexp = "SAVINGS|CHECKING")
    private String type;

    @NotNull @PositiveOrZero
    private BigDecimal initialBalance;

    @NotBlank @Pattern(regexp = "ACTIVE|INACTIVE")
    private String status;
}
