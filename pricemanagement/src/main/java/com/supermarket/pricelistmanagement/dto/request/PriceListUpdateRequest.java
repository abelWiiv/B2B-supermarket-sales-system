package com.supermarket.pricelistmanagement.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PriceListUpdateRequest {
    private UUID productId;
    private String customerCategory;
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    private LocalDate effectiveDate;
}