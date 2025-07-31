package com.banking.account.presentation;

import com.banking.account.application.AccountService;
import com.banking.account.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountCreateRequest request) {
        return accountService.create(request);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @PostMapping("/{id}/movements")
    public MovementResponse addMovement(@PathVariable Long id,
                                        @Valid @RequestBody MovementRequest request) {
        return accountService.registerMovement(id, request);
    }
}
