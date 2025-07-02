package com.supermarket.loyalty.service;

import com.supermarket.loyalty.entity.LoyaltyAccount;
import com.supermarket.loyalty.entity.LoyaltyTransaction;
import com.supermarket.loyalty.repository.LoyaltyAccountRepository;
import com.supermarket.loyalty.repository.LoyaltyTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class LoyaltyService {

    @Autowired
    private LoyaltyAccountRepository accountRepository;

    @Autowired
    private LoyaltyTransactionRepository transactionRepository;

    @Transactional
    public LoyaltyAccount createAccount(UUID customerId) {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setCustomerId(customerId);
        account.setPointsBalance(100);
        accountRepository.save(account);

        // Record initial points transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.EARN);
        transaction.setPoints(100);
        transaction.setDescription("Initial account creation");
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime()); // Set transaction date
        transactionRepository.save(transaction);

        return account;

    }

    @Transactional
    public void awardPoints(UUID customerId, Integer points) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));
        account.setPointsBalance(account.getPointsBalance() + points);

        // Record award transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.EARN);
        transaction.setPoints(points);
        transaction.setDescription("Points awarded");
        transactionRepository.save(transaction);

        accountRepository.save(account);
    }

    @Transactional
    public void redeemPoints(UUID customerId, Integer points) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));
        if (account.getPointsBalance() < points) {
            throw new RuntimeException("Insufficient points");
        }
        account.setPointsBalance(account.getPointsBalance() - points);

        // Record redeem transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.REDEEM);
        transaction.setPoints(points);
        transaction.setDescription("Points redeemed");
        transactionRepository.save(transaction);

        accountRepository.save(account);
    }

    public LoyaltyAccount getAccount(UUID customerId) {
        return accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));
    }

    @Transactional
    public void transferPoints(UUID fromCustomerId, UUID toCustomerId, Integer points) {
        if (fromCustomerId.equals(toCustomerId)) {
            throw new RuntimeException("Cannot transfer points to the same account");
        }

        LoyaltyAccount fromAccount = accountRepository.findByCustomerId(fromCustomerId)
                .orElseThrow(() -> new RuntimeException("Source loyalty account not found"));
        LoyaltyAccount toAccount = accountRepository.findByCustomerId(toCustomerId)
                .orElseThrow(() -> new RuntimeException("Target loyalty account not found"));

        if (fromAccount.getPointsBalance() < points) {
            throw new RuntimeException("Insufficient points in source account");
        }

        // Update balances
        fromAccount.setPointsBalance(fromAccount.getPointsBalance() - points);
        toAccount.setPointsBalance(toAccount.getPointsBalance() + points);

        // Record transfer transactions
        LoyaltyTransaction fromTransaction = new LoyaltyTransaction();
        fromTransaction.setCustomerId(fromCustomerId);
        fromTransaction.setTransactionType(LoyaltyTransaction.TransactionType.TRANSFER_OUT);
        fromTransaction.setPoints(points);
        fromTransaction.setDescription("Points transferred to customer " + toCustomerId);
        fromTransaction.setRelatedCustomerId(toCustomerId);
        transactionRepository.save(fromTransaction);

        LoyaltyTransaction toTransaction = new LoyaltyTransaction();
        toTransaction.setCustomerId(toCustomerId);
        toTransaction.setTransactionType(LoyaltyTransaction.TransactionType.TRANSFER_IN);
        toTransaction.setPoints(points);
        toTransaction.setDescription("Points received from customer " + fromCustomerId);
        toTransaction.setRelatedCustomerId(fromCustomerId);
        transactionRepository.save(toTransaction);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    public List<LoyaltyTransaction> getTransactionHistory(UUID customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
}