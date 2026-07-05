package com.github.harisherdiansyah.seapediaapi.features.wallets;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, UUID> {
    @Query("SELECT new com.github.harisherdiansyah.seapediaapi.features.wallets.TransactionListData(t.id, t.wallet.id, t.amount, t.balanceBeforeTransaction, t.balanceAfterTransaction, t.transactionType, t.createdAt) " +
            "FROM WalletTransactionEntity t " +
            "WHERE t.wallet.id = :walletId " +
            "ORDER BY t.createdAt DESC")
    Slice<TransactionListData> findWalletTransactionSlices(Pageable pageable, @Param("walletId") UUID walletId);
}
