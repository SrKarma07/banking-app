package com.banking.account.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account",
        uniqueConstraints = @UniqueConstraint(name = "uk_account_number", columnNames = "number"))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {

    public enum Type { SAVINGS, CHECKING }
    public enum Status { ACTIVE, INACTIVE }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",
            foreignKey = @ForeignKey(name = "fk_account_customer"))
    private Customer customer;


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Type type;

    @Column(nullable = false, scale = 2, precision = 18)
    private BigDecimal initialBalance;

    @Column(nullable = false, scale = 2, precision = 18)
    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status;
}
