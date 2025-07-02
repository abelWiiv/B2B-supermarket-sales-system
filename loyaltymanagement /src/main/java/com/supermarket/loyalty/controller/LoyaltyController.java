package com.supermarket.loyalty.controller;

import com.supermarket.loyalty.entity.LoyaltyAccount;
import com.supermarket.loyalty.entity.LoyaltyTransaction;
import com.supermarket.loyalty.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @PostMapping("/account/{customerId}")
    @PreAuthorize("hasAuthority('CREATE_SALES_ORDER')")
    public ResponseEntity<LoyaltyAccount> createAccount(@PathVariable UUID customerId) {
        LoyaltyAccount account = loyaltyService.createAccount(customerId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/account/{customerId}")
    @PreAuthorize("hasAuthority('CREATE_SALES_ORDER')")
    public ResponseEntity<LoyaltyAccount> getAccount(@PathVariable UUID customerId) {
        LoyaltyAccount account = loyaltyService.getAccount(customerId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/award/{customerId}")
    @PreAuthorize("hasAuthority('CREATE_SALES_ORDER')")
    public ResponseEntity<Void> awardPoints(@PathVariable UUID customerId, @RequestParam Integer points) {
        loyaltyService.awardPoints(customerId, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redeem/{customerId}")
    @PreAuthorize("hasAuthority('CREATE_SALES_ORDER')")
    public ResponseEntity<Void> redeemPoints(@PathVariable UUID customerId, @RequestParam Integer points) {
        loyaltyService.redeemPoints(customerId, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('CREATE_SALES_ORDER')")
    public ResponseEntity<Void> transferPoints(@RequestParam UUID fromCustomerId,
                                               @RequestParam UUID toCustomerId,
                                               @RequestParam Integer points) {
        loyaltyService.transferPoints(fromCustomerId, toCustomerId, points);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions/{customerId}")
    @PreAuthorize("hasAuthority('CREATE_SALES_ORDER')")
    public ResponseEntity<List<LoyaltyTransaction>> getTransactionHistory(@PathVariable UUID customerId) {
        List<LoyaltyTransaction> transactions = loyaltyService.getTransactionHistory(customerId);
        return ResponseEntity.ok(transactions);
    }
}