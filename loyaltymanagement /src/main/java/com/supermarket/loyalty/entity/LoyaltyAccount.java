package com.supermarket.loyalty.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "loyalty_account")
public class LoyaltyAccount {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "points_balance", nullable = false)
    private Integer pointsBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    public enum UserType {
        NORMAL(100, 1.0, 1.0),
        PREMIUM(200, 1.5, 1.2),
        VIP(500, 2.0, 1.0);

        private final int initialPoints;
        private final double awardMultiplier;
        private final double redeemMultiplier;

        UserType(int initialPoints, double awardMultiplier, double redeemMultiplier) {
            this.initialPoints = initialPoints;
            this.awardMultiplier = awardMultiplier;
            this.redeemMultiplier = redeemMultiplier;
        }

        public int getInitialPoints() {
            return initialPoints;
        }

        public double getAwardMultiplier() {
            return awardMultiplier;
        }

        public double getRedeemMultiplier() {
            return redeemMultiplier;
        }
    }

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}