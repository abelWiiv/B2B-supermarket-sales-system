package com.supermarket.pricelistmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PriceListCreateRequest {
    @NotNull(message = "Product ID is mandatory")
    private UUID productId;

    private String customerCategory;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Effective date is mandatory")
    private LocalDate effectiveDate;
}