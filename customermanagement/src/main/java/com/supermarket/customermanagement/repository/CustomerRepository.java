package com.supermarket.customermanagement.repository;

import com.supermarket.customermanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(String email);
    boolean existsByVatRegistrationNumber(String vatRegistrationNumber);
    Optional<Customer> findByEmail(String email);
}