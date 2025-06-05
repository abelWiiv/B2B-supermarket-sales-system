package com.supermarket.productmanagement.repository;

import com.supermarket.productmanagement.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Optional<Supplier> findByName(String name);
    boolean existsByName(String name);
}