package com.walletservice.walletservice.repository;

import com.walletservice.walletservice.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepo extends JpaRepository<WalletTransaction, Long> {

}
