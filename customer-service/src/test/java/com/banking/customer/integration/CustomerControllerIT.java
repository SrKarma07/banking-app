package com.banking.customer.integration;

import com.banking.customer.application.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerExtendedIT {

    @Autowired private TestRestTemplate rest;

    private static Long createdId;

    @Test @Order(1)
    void createCustomer_ShouldWork() {
        CustomerCreateRequest req = CustomerCreateRequest.builder()
                .person(PersonDTO.builder()
                        .identification("1900000001")
                        .firstName("Ana")
                        .lastName("Smith")
                        .age(25)
                        .address("Street 1")
                        .phone("0981111111")
                        .build())
                .password("pass123")
                .status("ACTIVE")
                .build();

        ResponseEntity<CustomerResponse> res =
                rest.postForEntity("/api/v1/customers", req, CustomerResponse.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        createdId = res.getBody().getId();
    }

    @Test @Order(2)
    void updateCustomer_ShouldReturn200() {
        CustomerUpdateRequest up = CustomerUpdateRequest.builder()
                .id(createdId)
                .person(PersonDTO.builder()
                        .identification("1900000001")   // unchanged
                        .firstName("Ana‑Maria")         // change name
                        .lastName("Smith")
                        .age(26)
                        .address("Street 1")
                        .phone("0981111111")
                        .build())
                .status("ACTIVE")
                .password("newpass")
                .build();

        HttpEntity<CustomerUpdateRequest> entity = new HttpEntity<>(up);
        ResponseEntity<CustomerResponse> res = rest.exchange(
                "/api/v1/customers/{id}", HttpMethod.PUT, entity,
                CustomerResponse.class, Map.of("id", createdId));

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getPerson().getFirstName()).isEqualTo("Ana‑Maria");
    }

    @Test @Order(3)
    void deleteCustomer_ShouldReturn204_AndThen404() {
        rest.delete("/api/v1/customers/{id}", createdId);

        ResponseEntity<CustomerResponse> notFound =
                rest.getForEntity("/api/v1/customers/{id}",
                        CustomerResponse.class, createdId);

        assertThat(notFound.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createCustomer_ShouldReturn400_WhenValidationFails() {
        CustomerCreateRequest invalid = CustomerCreateRequest.builder()
                .person(PersonDTO.builder()  // missing identification
                        .firstName("X")
                        .lastName("Y")
                        .age(-1)               // invalid age
                        .address("")
                        .phone("")
                        .build())
                .password("123")              // too short
                .status("WRONG")              // invalid enum
                .build();

        ResponseEntity<String> res =
                rest.postForEntity("/api/v1/customers", invalid, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
