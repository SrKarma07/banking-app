package com.banking.customer.unit;

import com.banking.common.exception.ConflictException;
import com.banking.common.exception.NotFoundException;
import com.banking.customer.application.dto.*;
import com.banking.customer.application.impl.CustomerServiceImpl;
import com.banking.customer.domain.model.Customer;
import com.banking.customer.domain.model.Person;
import com.banking.customer.domain.repository.CustomerRepository;
import com.banking.customer.domain.repository.PersonRepository;
import com.banking.customer.infrastructure.mapper.CustomerMapper;
import com.banking.customer.infrastructure.mapper.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock private PersonRepository personRepo;
    @Mock private CustomerRepository customerRepo;
    @Mock private PersonMapper personMapper;
    @Mock private CustomerMapper customerMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private CustomerServiceImpl service;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void create_ShouldPersist_WhenIdentificationIsUnique() {
        // Arrange
        PersonDTO personDto = PersonDTO.builder()
                .identification("1100110011")
                .firstName("John")
                .lastName("Doe")
                .age(30)
                .address("Main St")
                .phone("099999999")
                .build();

        CustomerCreateRequest req = CustomerCreateRequest.builder()
                .person(personDto)
                .password("secret123")
                .status("ACTIVE")
                .build();

        when(personRepo.existsByIdentification("1100110011")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hash");
        Person persistedPerson = Person.builder().id(1L).identification("1100110011").build();
        when(personMapper.toEntity(personDto)).thenReturn(persistedPerson);
        when(personRepo.save(any())).thenReturn(persistedPerson);

        Customer savedCustomer = Customer.builder().id(10L).person(persistedPerson).status(Customer.Status.ACTIVE).build();
        when(customerRepo.save(any())).thenReturn(savedCustomer);

        CustomerResponse expectedResponse = CustomerResponse.builder().id(10L).status("ACTIVE").build();
        when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

        // Act
        CustomerResponse result = service.create(req);

        // Assert
        assertThat(result.getId()).isEqualTo(10L);
        verify(customerRepo).save(any(Customer.class));
    }

    @Test
    void create_ShouldThrowConflict_WhenIdentificationExists() {
        // Arrange
        when(personRepo.existsByIdentification("1100110011")).thenReturn(true);

        CustomerCreateRequest req = CustomerCreateRequest.builder()
                .person(PersonDTO.builder().identification("1100110011").build())
                .password("x")
                .status("ACTIVE")
                .build();

        // Act / Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Identification already exists");
    }

    @Test
    void getById_ShouldThrowNotFound_WhenCustomerAbsent() {
        when(customerRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void update_ShouldThrowConflict_WhenNewIdentificationAlreadyExists() {
        // existente
        Person oldPerson = Person.builder()
                .id(1L).identification("1111").build();
        Customer existing = Customer.builder()
                .id(20L).person(oldPerson).status(Customer.Status.ACTIVE).build();
        when(customerRepo.findById(20L)).thenReturn(Optional.of(existing));

        // el nuevo número de cédula ya existe en BD
        when(personRepo.existsByIdentification("2222")).thenReturn(true);

        PersonDTO newPersonDTO = PersonDTO.builder()
                .identification("2222")
                .firstName("A").lastName("B").age(30)
                .address("X").phone("Y").build();

        CustomerUpdateRequest req = CustomerUpdateRequest.builder()
                .id(20L)
                .person(newPersonDTO)
                .status("ACTIVE")
                .build();

        assertThatThrownBy(() -> service.update(20L, req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Identification already exists");
    }

    @Test
    void changeStatus_ShouldPersist_WhenValid() {
        // Arrange
        Person p = Person.builder().id(1L).identification("3333").build();
        Customer c = Customer.builder()
                .id(30L).person(p).status(Customer.Status.ACTIVE).build();
        when(customerRepo.findById(30L)).thenReturn(Optional.of(c));
        when(customerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CustomerResponse expected = CustomerResponse.builder()
                .id(30L)
                .status("INACTIVE")
                .build();
        when(customerMapper.toResponse(any(Customer.class))).thenReturn(expected);  // ← NUEVO

        // Act
        CustomerResponse resp = service.changeStatus(30L, "INACTIVE");

        // Assert
        assertThat(resp.getStatus()).isEqualTo("INACTIVE");
        verify(customerRepo).save(argThat(cc ->
                cc.getStatus() == Customer.Status.INACTIVE));
    }
}
