package com.banking.customer.application;

import com.banking.customer.application.dto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse create(CustomerCreateRequest request);
    CustomerResponse update(Long id, CustomerUpdateRequest request);
    CustomerResponse getById(Long id);
    Page<CustomerResponse> list(Pageable pageable);
    void delete(Long id); // soft-delete not needed here, we inactivate instead
    CustomerResponse changeStatus(Long id, String status);
}
