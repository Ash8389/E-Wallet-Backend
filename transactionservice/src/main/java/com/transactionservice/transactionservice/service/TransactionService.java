package com.transactionservice.transactionservice.service;
import com.common.dto.Status;
import com.transactionservice.transactionservice.dto.TransactionRequest;
import com.transactionservice.transactionservice.model.Transaction;
import com.transactionservice.transactionservice.repository.TransactionRepo;
import com.transactionservice.transactionservice.service.wallet.WalletClientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private TransactionRepo transactionRepo;
    private WalletClientService walletClientService;
    private KafkaProducerService producerService;

    TransactionService(TransactionRepo transactionRepo, WalletClientService walletClientService, KafkaProducerService producerService){
        this.transactionRepo = transactionRepo;
        this.walletClientService = walletClientService;
        this.producerService = producerService;
    }

    public Transaction transfer(Long senderId,TransactionRequest request, String key){

        Optional<Transaction> te = transactionRepo.findByIdempotencyKey(key);
        
        if(te.isPresent()){
            System.out.println(te.get().getId());
            return te.get();
        }

        Transaction tx = new Transaction();

        tx.setReceiverId(request.getReceiverId());
        tx.setIdempotencyKey(key);
        tx.setSenderId(senderId);
        tx.setAmount(request.getAmount());
        tx.setStatus(Status.PENDING);
        tx.setSendAt(LocalDateTime.now());

        tx = transactionRepo.save(tx);

        Status st = walletClientService.transferService(senderId, request.getReceiverId(), request.getAmount(), tx.getId());
        tx.setStatus(st);
        transactionRepo.save(tx);

        producerService.produceEvent(st);

        return tx;
    }

//    @KafkaListener(topics = "transaction-topic-result", groupId = "tx-group")
//    public void handleResult(TransactionEvent event){
//        Transaction tx = transactionRepo.findById(event.getTransactionId())
//                .orElseThrow();
//
//        tx.setStatus(event.getStatus());
//
//        transactionRepo.save(tx);
//
//        System.out.println("Result Stored");
//    }


    public List<Transaction> getTransaction(Long userId){
        return transactionRepo.findBySenderId(userId);
    }
}
