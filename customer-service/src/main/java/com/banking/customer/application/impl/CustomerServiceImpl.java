package com.banking.customer.application.impl;

import com.banking.common.exception.ConflictException;
import com.banking.common.exception.NotFoundException;
import com.banking.customer.application.CustomerService;
import com.banking.customer.application.dto.*;
import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.Person;
import com.banking.customer.domain.repository.CustomerRepository;
import com.banking.customer.domain.repository.PersonRepository;
import com.banking.customer.infrastructure.mapper.CustomerMapper;
import com.banking.customer.infrastructure.mapper.PersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final PersonRepository personRepository;
    private final CustomerRepository customerRepository;
    private final PersonMapper personMapper;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CustomerResponse create(CustomerCreateRequest request) {
        log.info("Creating customer for identification={}", request.getPerson().getIdentification());

        if (personRepository.existsByIdentification(request.getPerson().getIdentification())) {
            throw new ConflictException("Identification already exists");
        }

        Person person = personMapper.toEntity(request.getPerson());
        person = personRepository.save(person);

        Customer customer = Customer.builder()
                .person(person)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(Customer.Status.valueOf(request.getStatus()))
                .build();

        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional
    @Override
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        log.info("Updating customer id={}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        // handle unique identification if changed
        String newIdf = request.getPerson().getIdentification();
        Person person = customer.getPerson();
        if (!person.getIdentification().equals(newIdf)
                && personRepository.existsByIdentification(newIdf)) {
            throw new ConflictException("Identification already exists");
        }

        // update person fields
        Person updated = personMapper.toEntity(request.getPerson());
        updated.setId(person.getId());
        updated = personRepository.save(updated);

        // update status and password if present
        customer.setPerson(updated);
        customer.setStatus(Customer.Status.valueOf(request.getStatus()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        customer = customerRepository.save(customer);

        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerResponse getById(Long id) {
        log.debug("Fetching customer id={}", id);
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CustomerResponse> list(Pageable pageable) {
        log.debug("Listing customers page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return customerRepository.findAll(pageable).map(customerMapper::toResponse);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.warn("Deleting customer id={}", id);
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found");
        }
        customerRepository.deleteById(id); // hard delete for now; or implement a soft delete flag if required
    }

    @Transactional
    @Override
    public CustomerResponse changeStatus(Long id, String status) {
        log.info("Changing status for customer id={} to {}", id, status);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        customer.setStatus(Customer.Status.valueOf(status));
        return customerMapper.toResponse(customerRepository.save(customer));
    }
}
