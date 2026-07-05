package com.github.harisherdiansyah.seapediaapi.features.wallets;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TopUpRequestDTO {
    @NotNull(message = "Wallet data cannot be empty")
    private UUID walletId;

    @NotNull(message = "Current balance is required")
    @Min(value = 0, message = "Current balance must be greater than or equal to 0")
    private BigDecimal currentBalance;

    @NotNull(message = "Amount is required")
    @Min(value = 10000, message = "Amount must be greater than or equal to 0")
    private BigDecimal amount;
}
