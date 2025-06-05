package com.supermarket.shopmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopUpdateRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private String location;

    private String managerContact;
}