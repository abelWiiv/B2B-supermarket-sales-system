package com.supermarket.loyalty.service;

import com.supermarket.loyalty.dto.LoyaltyAccountDto;
import com.supermarket.loyalty.entity.LoyaltyAccount;
import com.supermarket.loyalty.entity.LoyaltyTransaction;
import com.supermarket.loyalty.repository.LoyaltyAccountRepository;
import com.supermarket.loyalty.repository.LoyaltyTransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository transactionRepository;

    @Value("${loyalty.points.award-rate:0.01}")
    private Double pointsAwardRate;

    @Autowired
    public LoyaltyService(LoyaltyAccountRepository accountRepository,
                          LoyaltyTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;

    }

//    @PostConstruct
//    public void init() {
//        System.out.println(("Points award rate initialized to: {}" + pointsAwardRate));
//    }

    // Map LoyaltyAccount to LoyaltyAccountDto
    private LoyaltyAccountDto toDto(LoyaltyAccount account) {
        LoyaltyAccountDto dto = new LoyaltyAccountDto();
        dto.setId(account.getId());
        dto.setCustomerId(account.getCustomerId());
        dto.setPointsBalance(account.getPointsBalance());
        dto.setUserType(account.getUserType().name());
        return dto;
    }

    // Map LoyaltyAccountDto to LoyaltyAccount
    private LoyaltyAccount toEntity(LoyaltyAccountDto dto) {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setId(dto.getId());
        account.setCustomerId(dto.getCustomerId());
        account.setPointsBalance(dto.getPointsBalance());
        account.setUserType(LoyaltyAccount.UserType.valueOf(dto.getUserType()));
        return account;
    }

    @Transactional
    public LoyaltyAccountDto createAccount(UUID customerId, String userType) {
        LoyaltyAccount.UserType enumUserType;
        try {
            enumUserType = LoyaltyAccount.UserType.valueOf(userType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type: " + userType);
        }

        LoyaltyAccount account = new LoyaltyAccount();
        account.setCustomerId(customerId);
        account.setUserType(enumUserType);
        account.setPointsBalance(enumUserType.getInitialPoints());
        accountRepository.save(account);

        // Record initial points transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.EARN);
        transaction.setPoints(enumUserType.getInitialPoints());
        transaction.setDescription("Initial account creation - " + enumUserType.name());
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        transactionRepository.save(transaction);

        return toDto(account);
    }

//    @Transactional
//    public void awardPoints(UUID customerId, Integer points) {
//        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
//                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));
//        account.setPointsBalance(account.getPointsBalance() + points);
//
//        // Record award transaction
//        LoyaltyTransaction transaction = new LoyaltyTransaction();
//        transaction.setCustomerId(customerId);
//        transaction.setTransactionType(LoyaltyTransaction.TransactionType.EARN);
//        transaction.setPoints(points);
//        transaction.setDescription("Points awarded");
//        transactionRepository.save(transaction);
//
//        accountRepository.save(account);
//    }



    public Integer calculateAndAwardPoints(UUID customerId, BigDecimal totalBeforeDiscount) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found for customer ID: " + customerId));

        BigDecimal basePoints = totalBeforeDiscount.multiply(BigDecimal.valueOf(pointsAwardRate));
        double multiplier = account.getUserType().getAwardMultiplier();
        BigDecimal finalPoints = basePoints.multiply(BigDecimal.valueOf(multiplier));
        int pointsToAward = finalPoints.intValue();

        account.setPointsBalance(account.getPointsBalance() + pointsToAward);
        accountRepository.save(account);

        // Record the transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.EARN);
        transaction.setPoints(pointsToAward);
        transaction.setDescription("Points earned from purchase (UserType: " + account.getUserType().name() + ", Multiplier: " + multiplier + ")");
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        transactionRepository.save(transaction);

        return pointsToAward;
    }

    @Transactional
    public void redeemPoints(UUID customerId, Integer points) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));
        int adjustedPoints = (int) (points * account.getUserType().getRedeemMultiplier());
        if (account.getPointsBalance() < adjustedPoints) {
            throw new RuntimeException("Insufficient points");
        }
        account.setPointsBalance(account.getPointsBalance() - adjustedPoints);

        // Record redeem transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.REDEEM);
        transaction.setPoints(adjustedPoints);
        transaction.setDescription("Points redeemed (" + account.getUserType().name() + " multiplier)");
        transactionRepository.save(transaction);

        accountRepository.save(account);
    }


    public void reversePoints(UUID customerId, UUID transactionId){
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(()-> new RuntimeException("Loyalty account not found for customer id: " + customerId));

        LoyaltyTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found for id:" + transactionId));


        if(!transaction.getCustomerId().equals(customerId)){
            throw new RuntimeException("Transaction does not belong to the specified customer");
        }

        if (transaction.isReversed()) {
            throw new RuntimeException("Transaction already reversed");
        }

        int points = transaction.getPoints();
        LoyaltyTransaction.TransactionType type = transaction.getTransactionType();


        // Reverse the points based on transaction type
        if (type == LoyaltyTransaction.TransactionType.EARN) {
            if (account.getPointsBalance() < points) {
                throw new RuntimeException("Insufficient points to reverse award transaction");
            }
            account.setPointsBalance(account.getPointsBalance() - points);
        } else if (type == LoyaltyTransaction.TransactionType.REDEEM) {
            account.setPointsBalance(account.getPointsBalance() + points);
        } else {
            throw new RuntimeException("Cannot reverse transaction of type: " + type);
        }


        // Mark original transaction as reversed
        transaction.setReversed(true);
        transactionRepository.save(transaction);



        // Record reversal transaction
        LoyaltyTransaction reversalTransaction = new LoyaltyTransaction();
        reversalTransaction.setCustomerId(customerId);
        reversalTransaction.setTransactionType(LoyaltyTransaction.TransactionType.REVERSAL);
        reversalTransaction.setPoints(points);
        reversalTransaction.setDescription("Reversal of transaction ID: " + transactionId + " (" + type.name() + ")");
        reversalTransaction.setTransactionDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        reversalTransaction.setRelatedTransactionId(transactionId);
        transactionRepository.save(reversalTransaction);

        accountRepository.save(account);


    }

    public LoyaltyAccountDto getAccount(UUID customerId) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));
        return toDto(account);
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

        int fromAdjustedPoints = (int) (points * fromAccount.getUserType().getRedeemMultiplier());
        if (fromAccount.getPointsBalance() < fromAdjustedPoints) {
            throw new RuntimeException("Insufficient points in source account");
        }

        // Update balances
        fromAccount.setPointsBalance(fromAccount.getPointsBalance() - fromAdjustedPoints);
        toAccount.setPointsBalance(toAccount.getPointsBalance() + points);

        // Record transfer transactions
        LoyaltyTransaction fromTransaction = new LoyaltyTransaction();
        fromTransaction.setCustomerId(fromCustomerId);
        fromTransaction.setTransactionType(LoyaltyTransaction.TransactionType.TRANSFER_OUT);
        fromTransaction.setPoints(fromAdjustedPoints);
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

    @Transactional
    public LoyaltyAccountDto changeUserType(UUID customerId, String newUserType) {
        LoyaltyAccount account = accountRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Loyalty account not found"));

        LoyaltyAccount.UserType enumNewUserType;
        try {
            enumNewUserType = LoyaltyAccount.UserType.valueOf(newUserType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type: " + newUserType);
        }

        LoyaltyAccount.UserType oldUserType = account.getUserType();
        if (oldUserType == enumNewUserType) {
            throw new RuntimeException("New user type is the same as current user type");
        }

        account.setUserType(enumNewUserType);

        // Record user type change transaction
        LoyaltyTransaction transaction = new LoyaltyTransaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionType(LoyaltyTransaction.TransactionType.EARN);
        transaction.setPoints(0); // No points change, just recording the type change
        transaction.setDescription("User type changed from " + oldUserType.name() + " to " + enumNewUserType.name());
        transaction.setTransactionDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        transactionRepository.save(transaction);

        accountRepository.save(account);
        return toDto(account);
    }
}