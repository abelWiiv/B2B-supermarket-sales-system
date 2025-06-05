package com.supermarket.usermanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private boolean isActive;

    private Set<String> roles;
}