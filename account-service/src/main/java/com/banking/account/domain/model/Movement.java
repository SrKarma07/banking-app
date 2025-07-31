package com.banking.account.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "movement")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Movement {

    public enum Type { DEBIT, CREDIT }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",
            foreignKey = @ForeignKey(name = "fk_movement_account"))
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    private Type type;

    @Column(nullable = false, scale = 2, precision = 18)
    private BigDecimal amount;

    @Column(nullable = false, scale = 2, precision = 18)
    private BigDecimal balance; // balance después del movimiento

    @Column(nullable = false)
    private Instant date;
}
