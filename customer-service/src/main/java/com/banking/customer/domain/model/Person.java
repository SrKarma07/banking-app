package com.banking.customer.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "person",
        uniqueConstraints = @UniqueConstraint(name = "uk_person_identification",
                columnNames = "identification")
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String identification;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 1)
    private String gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 120)
    private String address;

    @Column(nullable = false, length = 30)
    private String phone;
}
