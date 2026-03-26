package com.walletservice.walletservice.repository;

import com.walletservice.walletservice.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    public Optional<Wallet> findByUserId(Long Id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId")
    public Optional<Wallet> findByUserIdForUpdate(Long userId);
}
