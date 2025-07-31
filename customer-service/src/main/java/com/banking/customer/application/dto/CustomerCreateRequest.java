package com.banking.customer.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateRequest {

    @NotNull
    private PersonDTO person;     // nested person payload

    @NotBlank @Size(min = 6, max = 64)
    private String password;

    @NotBlank @Pattern(regexp = "ACTIVE|INACTIVE")
    private String status;
}
