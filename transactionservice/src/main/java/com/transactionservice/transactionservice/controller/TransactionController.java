package com.transactionservice.transactionservice.controller;

import com.transactionservice.transactionservice.dto.TransactionRequest;
import com.transactionservice.transactionservice.model.Transaction;
import com.transactionservice.transactionservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private TransactionService transactionService;
    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody TransactionRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transfer(request));
    }

    @GetMapping("/{senderWalletId}")
    public ResponseEntity<List<Transaction>> transactions(@PathVariable Long senderWalletId){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransaction(senderWalletId));
    }
}
