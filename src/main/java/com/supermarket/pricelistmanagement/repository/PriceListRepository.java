package com.supermarket.pricelistmanagement.repository;

import com.supermarket.pricelistmanagement.model.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PriceListRepository extends JpaRepository<PriceList, UUID> {
    boolean existsByProductIdAndCustomerCategory(UUID productId, String customerCategory);
}