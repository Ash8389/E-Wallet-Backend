package com.walletservice.walletservice.controller;

import com.common.dto.Status;
import com.walletservice.walletservice.dtos.*;
import com.walletservice.walletservice.model.Wallet;
import com.walletservice.walletservice.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @PostMapping("/")
    public Wallet createWallet(@RequestHeader("X-User-Id") Long userId){
        return walletService.createWallet(userId);
    }

    @GetMapping("/")
    public Wallet getWallet(@RequestHeader("X-User-Id") Long userId){
        return walletService.getWallet(userId);
    }

    @GetMapping("/balance/")
    public BigDecimal getBalance(@RequestHeader("X-User-Id") Long userId) {
        return walletService.getBalance(userId);
    }

    @PutMapping("/credit")
    public ResponseEntity<CreditResponse> credit(@RequestBody CreditRequest creditRequest){
        return ResponseEntity.status(HttpStatus.OK).body(walletService.credit(creditRequest.getUserId(), creditRequest.getAmount()));
    }

    @PutMapping("/debit")
    public ResponseEntity<DebitResponse> debit(@RequestBody DebitRequest debitRequest){

        DebitResponse response = walletService.debit(debitRequest.getUserId(), debitRequest.getAmount() );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/transfer")
    public Status transfer(@Valid  @RequestBody TransferRequest transferRequest){

        System.out.println("transfer");
        return walletService.transfer(transferRequest.getSenderId(), transferRequest.getReceiverId(), transferRequest.getAmount(), transferRequest.getTransactionId());
    }
}
