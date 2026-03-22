package com.walletservice.walletservice.repository;

import com.walletservice.walletservice.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    public Optional<Wallet> findByUserId(Long Id);
}
