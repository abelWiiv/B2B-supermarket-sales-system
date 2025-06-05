package com.supermarket.productmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
}