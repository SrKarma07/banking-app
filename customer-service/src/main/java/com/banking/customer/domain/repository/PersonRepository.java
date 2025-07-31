package com.banking.customer.domain.repository;

import com.banking.customer.domain.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByIdentification(String identification);
    boolean existsByIdentification(String identification);
}
