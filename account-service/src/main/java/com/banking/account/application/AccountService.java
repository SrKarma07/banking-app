package com.banking.account.application;

import com.banking.account.application.dto.*;

public interface AccountService {
    AccountResponse create(AccountCreateRequest request);
    MovementResponse registerMovement(Long accountId, MovementRequest request);
    AccountResponse getById(Long id);
}
