package com.supermarket.shopmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopCreateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @NotBlank(message = "Manager contact is mandatory")
    private String managerContact;
}