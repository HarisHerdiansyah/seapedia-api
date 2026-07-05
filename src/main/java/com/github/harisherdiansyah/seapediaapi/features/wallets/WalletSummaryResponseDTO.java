package com.github.harisherdiansyah.seapediaapi.features.wallets;

import java.math.BigDecimal;
import java.util.List;

public record WalletSummaryResponseDTO(BigDecimal balance, int pageNumber, boolean hasNext, List<TransactionListData> transactions) {
}
