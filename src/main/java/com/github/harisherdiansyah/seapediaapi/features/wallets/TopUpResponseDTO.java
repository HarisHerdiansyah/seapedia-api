package com.github.harisherdiansyah.seapediaapi.features.wallets;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TopUpResponseDTO(BigDecimal amount, BigDecimal previousBalance, BigDecimal newBalance, OffsetDateTime timestamp) {
}
