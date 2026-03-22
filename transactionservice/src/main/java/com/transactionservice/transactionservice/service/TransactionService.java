package com.transactionservice.transactionservice.service;
import com.transactionservice.transactionservice.dto.TransactionRequest;
import com.transactionservice.transactionservice.model.Status;
import com.transactionservice.transactionservice.model.Transaction;
import com.transactionservice.transactionservice.repository.TransactionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private TransactionRepo transactionRepo;

    TransactionService(TransactionRepo transactionRepo){
        this.transactionRepo = transactionRepo;
    }

    public Transaction transfer(TransactionRequest request){
        Transaction tx = new Transaction();
        tx.setReceiverWalletId(request.getReceiverWalletId());
        tx.setSenderWalletId(request.getSenderWalletId());
        tx.setAmount(request.getAmount());
        tx.setStatus(Status.PENDING);
        tx.setSendAt(LocalDateTime.now());

        return transactionRepo.save(tx);
    }

    public List<Transaction> getTransaction(Long senderWalletId){
        return transactionRepo.findBySenderWalletId(senderWalletId);
    }

}
