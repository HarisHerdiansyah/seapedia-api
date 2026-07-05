package com.github.harisherdiansyah.seapediaapi.features.wallets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, UUID> {
}
