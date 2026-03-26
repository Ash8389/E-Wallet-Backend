package com.walletservice.walletservice.service;

import com.common.dto.Status;
import com.walletservice.walletservice.dtos.CreditRequest;
import com.walletservice.walletservice.dtos.CreditResponse;
import com.walletservice.walletservice.dtos.DebitRequest;
import com.walletservice.walletservice.dtos.DebitResponse;
import com.walletservice.walletservice.model.Wallet;
import com.walletservice.walletservice.repository.WalletRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepo walletRepo;

    WalletService(WalletRepo walletRepo){
        this.walletRepo = walletRepo;
    }

    public Wallet createWallet(Long userId){
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

    public CreditResponse credit(Long senderId , Double amount){
        Wallet wallet =  walletRepo.findByUserId(senderId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);

        wallet = walletRepo.save(wallet);
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

        DebitResponse debitResponse = new DebitResponse();
        debitResponse.setDebit_amount(amount);
        debitResponse.setTotal_amount(wallet.getBalance());

        return debitResponse;
    }

    @Transactional
    public Status transfer(Long senderId, Long receiverId, Double amount) {
        Wallet senderWallet = walletRepo.findByUserIdForUpdate(senderId)
                .orElseThrow();

        if(senderWallet.getBalance() < amount){
            return Status.FAILED;
        }

        Wallet receiverWallet = walletRepo.findByUserIdForUpdate(receiverId)
                .orElseThrow();

        senderWallet.setBalance(senderWallet.getBalance() - amount);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        return Status.DONE;
    }
}
