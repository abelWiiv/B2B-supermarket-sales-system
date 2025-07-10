package com.supermarket.salesmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.supermarket.salesmanagement.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // This ensures null fields are excluded from JSON
public class InvoiceResponse {
    private UUID id;
    private UUID salesOrderId;
    private LocalDate invoiceDate;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmount; // New field to store the total amount after redemption
    private BigDecimal redemptionAmount; // New field for the amount redeemed
    private int awardedPoints;
}