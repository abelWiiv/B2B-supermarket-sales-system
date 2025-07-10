package com.supermarket.loyalty.dto;

import java.util.UUID;

public class LoyaltyAccountDto {
    private UUID id;
    private UUID customerId;
    private Integer pointsBalance;
    private String userType; // e.g., "NORMAL", "PREMIUM", "VIP"

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Integer getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(Integer pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}