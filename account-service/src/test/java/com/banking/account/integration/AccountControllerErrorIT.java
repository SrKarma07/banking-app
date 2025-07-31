package com.banking.account.integration;

import com.banking.account.application.dto.MovementRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerErrorIT {

    @Autowired private TestRestTemplate rest;

    @Test
    void debit_ShouldReturn409_WhenInsufficient() {
        // 1. crear cuenta con saldo 0
        var acc = rest.postForEntity("/api/v1/accounts",
                new com.banking.account.application.dto.AccountCreateRequest(
                        "ERR‑1", "SAVINGS", BigDecimal.ZERO, "ACTIVE"),
                com.banking.account.application.dto.AccountResponse.class).getBody();

        // 2. intentar débito
        MovementRequest req = MovementRequest.builder()
                .type("DEBIT").amount(BigDecimal.TEN).build();

        ResponseEntity<String> res = rest.postForEntity(
                "/api/v1/accounts/{id}/movements", req, String.class, acc.getId());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getAccount_ShouldReturn404_WhenIdDoesNotExist() {
        ResponseEntity<String> res = rest.getForEntity(
                "/api/v1/accounts/{id}", String.class, 99999);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

