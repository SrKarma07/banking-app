package com.banking.account.domain.model;

import jakarta.persistence.*;
import lombok.*;

/**  Entidad *solo* para referencia FK al esquema customer. */
@Entity
@Table(name = "customer", schema = "customer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
