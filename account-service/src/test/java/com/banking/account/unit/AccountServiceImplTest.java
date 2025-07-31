package com.banking.account.unit;

import com.banking.account.application.dto.*;
import com.banking.account.application.impl.AccountServiceImpl;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.Movement;
import com.banking.account.domain.repository.AccountRepository;
import com.banking.account.domain.repository.MovementRepository;
import com.banking.account.infrastructure.mapper.AccountMapper;
import com.banking.account.infrastructure.mapper.MovementMapper;
import com.banking.common.exception.ConflictException;
import com.banking.common.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock private AccountRepository accountRepo;
    @Mock private MovementRepository movementRepo;
    @Mock private AccountMapper accountMapper;
    @Mock private MovementMapper movementMapper;

    @InjectMocks private AccountServiceImpl service;

    @BeforeEach
    void initMocks() { MockitoAnnotations.openMocks(this); }

    /* ---------- create() ---------- */

    @Test
    void create_ShouldPersist_WhenNumberUnique() {
        AccountCreateRequest req = AccountCreateRequest.builder()
                .number("ACC‑001")
                .type("SAVINGS")
                .initialBalance(BigDecimal.valueOf(100))
                .status("ACTIVE")
                .build();

        when(accountRepo.existsByNumber("ACC‑001")).thenReturn(false);

        Account entity = Account.builder()
                .id(1L).number("ACC‑001")
                .type(Account.Type.SAVINGS)
                .initialBalance(BigDecimal.valueOf(100))
                .currentBalance(BigDecimal.valueOf(100))
                .status(Account.Status.ACTIVE)
                .build();
        when(accountMapper.toEntity(req)).thenReturn(entity);
        when(accountRepo.save(entity)).thenReturn(entity);

        AccountResponse resp = AccountResponse.builder()
                .id(1L).number("ACC‑001")
                .type("SAVINGS")
                .currentBalance(BigDecimal.valueOf(100))
                .status("ACTIVE")
                .build();
        when(accountMapper.toResponse(entity)).thenReturn(resp);

        AccountResponse result = service.create(req);

        assertThat(result.getId()).isEqualTo(1L);
        verify(accountRepo).save(any());
    }

    @Test
    void create_ShouldThrowConflict_WhenNumberExists() {
        when(accountRepo.existsByNumber("DUP‑01")).thenReturn(true);

        AccountCreateRequest req = AccountCreateRequest.builder()
                .number("DUP‑01")
                .type("CHECKING")
                .initialBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("number already exists");
    }

    /* ---------- registerMovement() ---------- */

    @Test
    void debit_ShouldUpdateBalance_WhenEnoughFunds() {
        Account acc = Account.builder()
                .id(5L).number("ACC‑5")
                .currentBalance(BigDecimal.valueOf(200))
                .status(Account.Status.ACTIVE)
                .type(Account.Type.SAVINGS)
                .build();
        when(accountRepo.findById(5L)).thenReturn(Optional.of(acc));
        when(accountRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Movement saved = Movement.builder()
                .id(10L).account(acc)
                .type(Movement.Type.DEBIT)
                .amount(BigDecimal.valueOf(50))
                .balance(BigDecimal.valueOf(150))
                .date(Instant.now())
                .build();
        when(movementRepo.save(any())).thenReturn(saved);

        MovementResponse expected = MovementResponse.builder()
                .id(10L).type("DEBIT")
                .amount(BigDecimal.valueOf(50))
                .balance(BigDecimal.valueOf(150))
                .date(saved.getDate())
                .build();
        when(movementMapper.toResponse(saved)).thenReturn(expected);

        MovementRequest req = MovementRequest.builder()
                .type("DEBIT")
                .amount(BigDecimal.valueOf(50))
                .build();

        MovementResponse resp = service.registerMovement(5L, req);

        assertThat(resp.getBalance()).isEqualTo(BigDecimal.valueOf(150));
        verify(accountRepo).save(argThat(a -> a.getCurrentBalance()
                .equals(BigDecimal.valueOf(150))));
    }

    @Test
    void debit_ShouldThrowConflict_WhenInsufficientFunds() {
        Account acc = Account.builder()
                .id(6L).currentBalance(BigDecimal.valueOf(10))
                .type(Account.Type.SAVINGS)
                .status(Account.Status.ACTIVE)
                .build();
        when(accountRepo.findById(6L)).thenReturn(Optional.of(acc));

        MovementRequest req = MovementRequest.builder()
                .type("DEBIT").amount(BigDecimal.valueOf(20)).build();

        assertThatThrownBy(() -> service.registerMovement(6L, req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void registerMovement_ShouldThrowNotFound_WhenAccountMissing() {
        when(accountRepo.findById(99L)).thenReturn(Optional.empty());

        MovementRequest req = MovementRequest.builder()
                .type("CREDIT").amount(BigDecimal.TEN).build();

        assertThatThrownBy(() -> service.registerMovement(99L, req))
                .isInstanceOf(NotFoundException.class);
    }
}
