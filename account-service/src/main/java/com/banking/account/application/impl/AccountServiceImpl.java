package com.banking.account.application.impl;

import com.banking.account.application.AccountService;
import com.banking.account.application.dto.*;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Movement;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.MovementRepository;
import com.banking.account.infrastructure.mapper.AccountMapper;
import com.banking.account.infrastructure.mapper.MovementMapper;
import com.banking.common.exception.ConflictException;
import com.banking.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final AccountMapper accountMapper;
    private final MovementMapper movementMapper;

    @Transactional
    @Override
    public AccountResponse create(AccountCreateRequest request) {
        log.info("Creating account number={}", request.getNumber());

        if (accountRepository.existsByNumber(request.getNumber())) {
            throw new ConflictException("Account number already exists");
        }

        Account account = accountMapper.toEntity(request);
        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Transactional
    @Override
    public MovementResponse registerMovement(Long accountId, MovementRequest request) {
        log.info("Registering {} of {} for accountId={}", request.getType(),
                request.getAmount(), accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        BigDecimal amount = request.getAmount();
        Movement.Type type = Movement.Type.valueOf(request.getType());
        BigDecimal newBalance = switch (type) {
            case DEBIT  -> account.getCurrentBalance().subtract(amount);
            case CREDIT -> account.getCurrentBalance().add(amount);
        };

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ConflictException("Insufficient balance");
        }

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        Movement movement = Movement.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balance(newBalance)
                .date(Instant.now())
                .build();

        movement = movementRepository.save(movement);
        return movementMapper.toResponse(movement);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountResponse getById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }
}
