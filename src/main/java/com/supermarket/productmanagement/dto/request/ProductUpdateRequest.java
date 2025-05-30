package com.supermarket.productmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductUpdateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private UUID categoryId;

    private UUID supplierId;

    @NotBlank(message = "Unit of measure is mandatory")
    private String unitOfMeasure;
}