package com.banking.account.domain.repository;

import com.banking.account.domain.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findByAccount_IdOrderByDateDesc(Long accountId);
}
