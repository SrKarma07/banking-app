package com.banking.account.domain.repository;

import com.banking.account.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNumber(String number);
    Optional<Account> findByNumber(String number);
}
