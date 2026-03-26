package com.transactionservice.transactionservice.repository;

import com.transactionservice.transactionservice.model.Transaction;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderId(Long walletId);

    Optional<Transaction> findByIdempotencyKey(String key);
}
