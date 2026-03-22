package com.walletservice.walletservice.service;

import com.walletservice.walletservice.dtos.CreditRequest;
import com.walletservice.walletservice.dtos.CreditResponse;
import com.walletservice.walletservice.dtos.DebitRequest;
import com.walletservice.walletservice.dtos.DebitResponse;
import com.walletservice.walletservice.model.Wallet;
import com.walletservice.walletservice.repository.WalletRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public CreditResponse credit(CreditRequest creditRequest){
        Wallet wallet =  walletRepo.findByUserId(creditRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + creditRequest.getAmount());

        wallet = walletRepo.save(wallet);
        CreditResponse creditResponse = new CreditResponse();
        creditResponse.setCredit_amount(creditRequest.getAmount());
        creditResponse.setTotal_amount(wallet.getBalance());

        return creditResponse;
    }

    public DebitResponse debit(DebitRequest debitRequest){
        Wallet wallet =  walletRepo.findByUserId(debitRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if(wallet.getBalance() < debitRequest.getAmount()){
            return null;
        }

        wallet.setBalance(wallet.getBalance() - debitRequest.getAmount());
        walletRepo.save(wallet);

        DebitResponse debitResponse = new DebitResponse();
        debitResponse.setDebit_amount(debitRequest.getAmount());
        debitResponse.setTotal_amount(wallet.getBalance());

        return debitResponse;
    }
}
