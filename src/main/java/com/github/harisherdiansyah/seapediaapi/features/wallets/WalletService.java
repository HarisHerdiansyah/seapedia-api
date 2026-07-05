package com.github.harisherdiansyah.seapediaapi.features.wallets;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletSummaryResponseDTO getWalletSummary(Pageable pageable) {
        UUID userId = SecurityUtil.getCurrentUserId();

        WalletEntity userWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet not found for user with ID: " + userId));
        UUID walletId = userWallet.getId();
        BigDecimal balance = userWallet.getBalance();

        Slice<TransactionListData> transactionSlice = walletTransactionRepository.findWalletTransactionSlices(pageable, walletId);
        int pageNumber = transactionSlice.getNumber();
        boolean hasNext = transactionSlice.hasNext();
        List<TransactionListData> transactions = transactionSlice.getContent();

        return new WalletSummaryResponseDTO(balance, pageNumber, hasNext, transactions);
    }

    @Transactional
    public TopUpResponseDTO topUpWallet(TopUpRequestDTO topUpRequestDTO) {
        UUID walletId = topUpRequestDTO.getWalletId();
        WalletEntity walletEntity = walletRepository.findWalletEntityById(walletId)
                .orElseThrow(() -> new NotFoundException("Wallet not found with ID: " + walletId));

        BigDecimal amount = topUpRequestDTO.getAmount();
        BigDecimal newBalance = walletEntity.getBalance().add(amount);

        WalletTransactionEntity walletTransactionEntity = WalletTransactionEntity.builder()
                .wallet(walletEntity)
                .amount(topUpRequestDTO.getAmount())
                .balanceBeforeTransaction(topUpRequestDTO.getCurrentBalance())
                .balanceAfterTransaction(topUpRequestDTO.getCurrentBalance().add(topUpRequestDTO.getAmount()))
                .transactionType(WalletTransactionType.TOP_UP)
                .build();
        walletTransactionRepository.save(walletTransactionEntity);

        walletEntity.setBalance(newBalance);
        walletRepository.save(walletEntity);

        return new TopUpResponseDTO(
                walletTransactionEntity.getAmount(),
                walletTransactionEntity.getBalanceBeforeTransaction(),
                walletTransactionEntity.getBalanceAfterTransaction(),
                walletTransactionEntity.getCreatedAt()
        );
    }
}
