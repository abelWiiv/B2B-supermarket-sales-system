package com.supermarket.usermanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PrivilegeResponse {

    private UUID id;

    private String name;

    private String description;
}