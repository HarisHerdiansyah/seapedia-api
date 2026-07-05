package com.github.harisherdiansyah.seapediaapi.features.wallets;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionListData(UUID id, UUID walletId, BigDecimal amount, BigDecimal balanceBeforeTransaction, BigDecimal balanceAfterTransaction, WalletTransactionType transactionType, OffsetDateTime createdAt) {
}
