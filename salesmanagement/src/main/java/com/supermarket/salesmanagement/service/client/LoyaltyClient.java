package com.supermarket.salesmanagement.service.client;

import com.supermarket.salesmanagement.config.FeignClientConfig;
import com.supermarket.salesmanagement.dto.LoyaltyAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "loyalty-management", url ="${application.loyalty.service.url}", configuration = FeignClientConfig.class)
//@FeignClient(name = "product-management", url = "${application.product.service.url}", configuration = FeignClientConfig.class)

public interface LoyaltyClient {

    @PostMapping("/api/v1/loyalty/account")
    LoyaltyAccount createAccount(@RequestBody LoyaltyAccount loyaltyAccount);

    @GetMapping("/api/v1/loyalty/account/{customerId}")
    LoyaltyAccount getAccount(@PathVariable("customerId") UUID customerId);

    @PostMapping("/api/v1/loyalty/account/{customerId}/award")
    void awardPoints(@PathVariable("customerId") UUID customerId, @RequestParam("points") Integer points);

//    @PostMapping("/api/v1/loyalty/award/{customerId}")
//    Integer calculateAndAwardPoints(@PathVariable("customerId") UUID customerId, @RequestParam("totalBeforeDiscount") BigDecimal totalBeforeDiscount);

    @PostMapping("/api/v1/loyalty/redeem/{customerId}")
    void redeemPoints(@PathVariable("customerId") UUID customerId, @RequestParam("points") Integer points);

    @PostMapping("/api/v1/loyalty/award/{customerId}")
    Integer calculateAndAwardPoints(@PathVariable("customerId") UUID customerId,
                                    @RequestParam("totalBeforeDiscount") BigDecimal totalBeforeDiscount);


    @PostMapping("/api/v1/loyalty/reverse/{customerId}")
    void reversePoints(@PathVariable("customerId") UUID customerId, @RequestParam("transactionId") UUID transactionId);
}