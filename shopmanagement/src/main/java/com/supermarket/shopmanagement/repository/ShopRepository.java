package com.supermarket.shopmanagement.repository;

import com.supermarket.shopmanagement.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {
    Optional<Shop> findByName(String name);
    boolean existsByName(String name);
}