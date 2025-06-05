package com.supermarket.usermanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RoleResponse {

    private UUID id;

    private String name;

    private String description;

    private Set<String> privileges;
}