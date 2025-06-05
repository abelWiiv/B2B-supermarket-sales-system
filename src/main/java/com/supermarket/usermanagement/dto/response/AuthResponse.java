package com.supermarket.usermanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    private String username;

    private String email;
}