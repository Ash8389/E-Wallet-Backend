package com.transactionservice.transactionservice.repository;

import com.transactionservice.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderWalletId(Long walletId);
}
