package com.banking.customer.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateRequest {

    @NotNull
    private Long id;

    @NotNull
    private PersonDTO person;

    @Size(min = 6, max = 64)
    private String password; // optional

    @NotBlank @Pattern(regexp = "ACTIVE|INACTIVE")
    private String status;
}
