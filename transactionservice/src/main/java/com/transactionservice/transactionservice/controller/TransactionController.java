package com.transactionservice.transactionservice.controller;

import com.transactionservice.transactionservice.dto.TransactionRequest;
import com.transactionservice.transactionservice.model.Transaction;
import com.transactionservice.transactionservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionService transactionService;

    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestHeader("X-User-Id") Long senderId,
            @RequestBody TransactionRequest request,
            @RequestHeader(name = "Idempotency-Key") String key
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transfer(senderId, request, key));
    }

    @GetMapping
    public ResponseEntity<List<?>> transactions(@RequestHeader(name = "X-User-Id") Long userId){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransaction(userId));
    }
}
