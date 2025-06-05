package com.supermarket.productmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    @NotNull(message = "Category ID is mandatory")
    private UUID categoryId;

    @NotNull(message = "Supplier ID is mandatory")
    private UUID supplierId;

    @NotBlank(message = "Unit of measure is mandatory")
    private String unitOfMeasure;
}