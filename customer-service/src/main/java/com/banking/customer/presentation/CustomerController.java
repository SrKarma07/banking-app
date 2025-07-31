package com.banking.customer.presentation;

import com.banking.customer.application.CustomerService;
import com.banking.customer.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest request) {
        return customerService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id,
                                   @Valid @RequestBody CustomerUpdateRequest request) {
        request.setId(id);
        return customerService.update(id, request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    @GetMapping
    public Page<CustomerResponse> list(@ParameterObject @RequestParam(defaultValue = "0") int page,
                                       @ParameterObject @RequestParam(defaultValue = "10") int size) {
        return customerService.list(PageRequest.of(page, size));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @PatchMapping("/{id}/status/{status}")
    public CustomerResponse changeStatus(@PathVariable Long id, @PathVariable String status) {
        return customerService.changeStatus(id, status);
    }
}
