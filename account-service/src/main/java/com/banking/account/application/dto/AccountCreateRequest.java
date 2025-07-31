package com.banking.account.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor          // (customerId, number, type, initialBalance, status)
public class AccountCreateRequest {

    /** id del dueño de la cuenta */
    @NotNull
    private Long customerId;

    @NotBlank @Size(max = 20)
    private String number;

    @NotBlank @Pattern(regexp = "SAVINGS|CHECKING")
    private String type;

    @NotNull @PositiveOrZero
    private BigDecimal initialBalance;

    @NotBlank @Pattern(regexp = "ACTIVE|INACTIVE")
    private String status;

    /* ----------------------------------------------------------------
       Compatibilidad con los tests existentes  ––  constructor antiguo
       (nº, tipo, saldo, estado)  ── rellena customerId con null
     ----------------------------------------------------------------*/
    public AccountCreateRequest(String number,
                                String type,
                                BigDecimal initialBalance,
                                String status) {
        this(null, number, type, initialBalance, status);
    }
}
