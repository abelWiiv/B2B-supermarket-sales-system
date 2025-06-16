package com.supermarket.pricelistmanagement.repository;

import com.supermarket.pricelistmanagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PriceListRepository extends JpaRepository<PriceList, UUID> {
    boolean existsByProductIdAndCustomerCategory(UUID productId, String customerCategory);

    @Query("SELECT p FROM PriceList p WHERE p.productId = :productId")
    Optional<PriceList> findByProductId(UUID productId);
}