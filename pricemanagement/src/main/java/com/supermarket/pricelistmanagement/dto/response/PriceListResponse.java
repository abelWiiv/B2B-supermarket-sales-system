package com.supermarket.pricelistmanagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PriceListResponse {
    private UUID id;
    private UUID productId;
    private String customerCategory;
    private BigDecimal price;
    private LocalDate effectiveDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}