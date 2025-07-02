package com.supermarket.salesmanagement.dto;

import java.util.UUID;

public class LoyaltyAccount {
    private UUID customerId;
    private Integer pointsBalance;

    // Getters and setters
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public Integer getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(Integer pointsBalance) { this.pointsBalance = pointsBalance; }
}