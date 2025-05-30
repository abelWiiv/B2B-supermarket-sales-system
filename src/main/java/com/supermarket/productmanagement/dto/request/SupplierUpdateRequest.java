package com.supermarket.productmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierUpdateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Contact details are mandatory")
    private String contactDetails;
}