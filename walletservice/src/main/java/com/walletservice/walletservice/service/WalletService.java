package com.walletservice.walletservice.service;

import com.common.dto.Status;
import com.walletservice.walletservice.dtos.CreditRequest;
import com.walletservice.walletservice.dtos.CreditResponse;
import com.walletservice.walletservice.dtos.DebitRequest;
import com.walletservice.walletservice.dtos.DebitResponse;
import com.walletservice.walletservice.model.Wallet;
import com.walletservice.walletservice.model.WalletTransaction;
import com.walletservice.walletservice.repository.WalletRepo;
import com.walletservice.walletservice.repository.WalletTransactionRepo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepo walletRepo;
    private final WalletTransactionRepo walletTransactionRepo;
    private final StringRedisTemplate redisTemplate;


    WalletService(WalletRepo walletRepo, WalletTransactionRepo walletTransactionRepo, StringRedisTemplate redisTemplate){
        this.walletRepo = walletRepo;
        this.walletTransactionRepo = walletTransactionRepo;
        this.redisTemplate = redisTemplate;
    }

    public Wallet createWallet(Long userId){
        Optional<Wallet> exist = walletRepo.findByUserId(userId);
        if(exist.isPresent()){
            return exist.get();
        }

        Wallet wallet = new Wallet();

        wallet.setUserId(userId);
        wallet.setBalance(0.0);
        wallet.setCurrency("INR");
        wallet.setCreatedAt(LocalDateTime.now());

        return walletRepo.save(wallet);
    }

    public Wallet getWallet(Long userId){
        return walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public Double getBalance(Long userId){

        String key = "balance:" + userId;

        String bal = redisTemplate.opsForValue().get(key);
        if(bal != null && !bal.isEmpty()){
            return Double.parseDouble(bal);
        }

        Wallet wallet = walletRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        redisTemplate.opsForValue().set(key, String.valueOf(wallet.getBalance()), Duration.ofMinutes(5));

        return wallet.getBalance();
    }

    public CreditResponse credit(Long senderId , Double amount){
        Wallet wallet =  walletRepo.findByUserId(senderId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);
        wallet = walletRepo.save(wallet);
        deleteCache(senderId);

        CreditResponse creditResponse = new CreditResponse();
        creditResponse.setCredit_amount(amount);
        creditResponse.setTotal_amount(wallet.getBalance());

        return creditResponse;
    }

    public DebitResponse debit(Long receiverId, Double amount){
        Wallet wallet =  walletRepo.findByUserId(receiverId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if(wallet.getBalance() < amount){
            return null;
        }

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepo.save(wallet);
        deleteCache(receiverId);

        DebitResponse debitResponse = new DebitResponse();
        debitResponse.setDebit_amount(amount);
        debitResponse.setTotal_amount(wallet.getBalance());

        return debitResponse;
    }

    @Transactional
    public Status transfer(Long senderId, Long receiverId, Double amount, Long transactionId) {
        Wallet senderWallet = walletRepo.findByUserIdForUpdate(senderId)
                .orElseThrow();

        if(senderWallet.getBalance() < amount){
            return Status.FAILED;
        }

        Wallet receiverWallet = walletRepo.findByUserIdForUpdate(receiverId)
                .orElseThrow();

        senderWallet.setBalance(senderWallet.getBalance() - amount);
        WalletTransaction debit = new WalletTransaction(
                senderWallet.getId(),
                amount,
                "Debit",
                transactionId
        );
        deleteCache(senderId);
        walletTransactionRepo.save(debit);

        receiverWallet.setBalance(receiverWallet.getBalance() + amount);
        WalletTransaction credit = new WalletTransaction(
                receiverWallet.getId(),
                amount,
                "Credit",
                transactionId
        );
        deleteCache(receiverId);
        walletTransactionRepo.save(credit);

        walletRepo.save(senderWallet);
        walletRepo.save(receiverWallet);

        return Status.DONE;
    }

    public void deleteCache(Long userId){
        String key = "balance:" + userId;

        redisTemplate.delete(key);
    }
}
