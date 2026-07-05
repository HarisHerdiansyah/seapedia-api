package com.github.harisherdiansyah.seapediaapi.features.wallets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public TopUpResponseDTO topUpWallet(TopUpRequestDTO topUpRequestDTO) {
        UUID walletId = topUpRequestDTO.getWalletId();
        WalletEntity walletEntity = walletRepository.getReferenceById(walletId);

        WalletTransactionEntity walletTransactionEntity = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .amount(topUpRequestDTO.getAmount())
                .balanceBeforeTransaction(topUpRequestDTO.getCurrentBalance())
                .balanceAfterTransaction(topUpRequestDTO.getCurrentBalance().add(topUpRequestDTO.getAmount()))
                .transactionType(WalletTransactionType.TOP_UP)
                .build();

        walletTransactionRepository.save(walletTransactionEntity);

        return new TopUpResponseDTO(
                walletTransactionEntity.getAmount(),
                walletTransactionEntity.getBalanceBeforeTransaction(),
                walletTransactionEntity.getBalanceAfterTransaction(),
                walletTransactionEntity.getCreatedAt()
        );
    }
}
