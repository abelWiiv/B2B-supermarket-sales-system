package com.supermarket.productmanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SupplierResponse {
    private UUID id;
    private String name;
    private String contactDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}