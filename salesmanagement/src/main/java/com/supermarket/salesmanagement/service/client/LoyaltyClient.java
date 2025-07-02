package com.supermarket.salesmanagement.service.client;

import com.supermarket.salesmanagement.config.FeignClientConfig;
import com.supermarket.salesmanagement.dto.LoyaltyAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "loyalty-management", url = "${application.loyalty.service.url}", configuration = FeignClientConfig.class)
public interface LoyaltyClient {
    @GetMapping("/api/v1/loyalty/account/{customer_id}")
    LoyaltyAccount getLoyaltyById(@PathVariable("customer_id") UUID customerId);

    @PostMapping("/api/v1/loyalty/redeem/{customer_id}")
    void redeemPoints(@PathVariable("customer_id") UUID customerId, @RequestParam("points") Integer points);

    @PostMapping("/api/v1/loyalty/award/{customer_id}")
    void awardPoints(@PathVariable("customer_id") UUID customerId, @RequestParam("points") Integer points);
}