package com.banking.account.integration;

import com.banking.account.application.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerIT {

    @Autowired private TestRestTemplate rest;
    private static Long accId;

    @Test @Order(1)
    void createAccount_ShouldReturn201() {
        AccountCreateRequest req = AccountCreateRequest.builder()
                .number("IT‑001")
                .type("CHECKING")
                .initialBalance(BigDecimal.valueOf(300))
                .status("ACTIVE")
                .build();

        ResponseEntity<AccountResponse> res =
                rest.postForEntity("/api/v1/accounts", req, AccountResponse.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        accId = res.getBody().getId();
        assertThat(res.getBody().getCurrentBalance()).isEqualTo(BigDecimal.valueOf(300));
    }

    @Test @Order(2)
    void creditMovement_ShouldIncreaseBalance() {
        MovementRequest req = MovementRequest.builder()
                .type("CREDIT").amount(BigDecimal.valueOf(50)).build();

        ResponseEntity<MovementResponse> res = rest.postForEntity(
                "/api/v1/accounts/{id}/movements", req,
                MovementResponse.class, Map.of("id", accId));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(350));
    }

    @Test @Order(3)
    void debitMovement_ShouldDecreaseBalance() {
        MovementRequest req = MovementRequest.builder()
                .type("DEBIT").amount(BigDecimal.valueOf(100)).build();

        ResponseEntity<MovementResponse> res = rest.postForEntity(
                "/api/v1/accounts/{id}/movements", req,
                MovementResponse.class, Map.of("id", accId));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(250));
    }
}
