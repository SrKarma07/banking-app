package com.banking.account.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String number;
    private String type;
    private BigDecimal currentBalance;
    private String status;
}
