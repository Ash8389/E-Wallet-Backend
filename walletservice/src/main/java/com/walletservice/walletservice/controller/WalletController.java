package com.walletservice.walletservice.controller;

import com.common.dto.Status;
import com.walletservice.walletservice.dtos.*;
import com.walletservice.walletservice.model.Wallet;
import com.walletservice.walletservice.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @PostMapping("/{userId}")
    public Wallet createWallet(@PathVariable Long userId){
        return walletService.createWallet(userId);
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId){
        return walletService.getWallet(userId);
    }

    @PutMapping("/credit")
    public ResponseEntity<CreditResponse> credit(@RequestBody CreditRequest creditRequest){
        return ResponseEntity.status(HttpStatus.OK).body(walletService.credit(creditRequest.getUserId(), creditRequest.getAmount()));
    }

    @PutMapping("/debit")
    public ResponseEntity<DebitResponse> debit(@RequestBody DebitRequest debitRequest){

        System.out.println("Debit");
        DebitResponse debit = walletService.debit(debitRequest.getUserId(), debitRequest.getAmount() );

        if(debit == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(debit);
    }

    @PostMapping("/transfer")
    public Status transfer(@RequestBody TransferRequest transferRequest){

        System.out.println("transfer");
        return walletService.transfer(transferRequest.getSenderId(), transferRequest.getReceiverId(), transferRequest.getAmount(), transferRequest.getTransactionId());
    }
}
