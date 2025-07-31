package com.banking.customer.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {

    private Long id;

    @NotBlank @Size(max = 20)
    private String identification;

    @NotBlank @Size(max = 80)
    private String firstName;

    @NotBlank @Size(max = 80)
    private String lastName;

    /** NUEVO ➜ “M” ó “F” */
    @NotBlank
    @Pattern(regexp = "M|F")
    private String gender;

    @NotNull @Min(0) @Max(120)
    private Integer age;

    @NotBlank @Size(max = 120)
    private String address;

    @NotBlank @Size(max = 30)
    private String phone;
}
