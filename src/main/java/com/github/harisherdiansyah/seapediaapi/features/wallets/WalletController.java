package com.github.harisherdiansyah.seapediaapi.features.wallets;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PreAuthorize("hasAuthority('MANAGE_WALLET')")
    @PostMapping("/top-up")
    public ResponseEntity<?> topUpWallet(@Valid @RequestBody TopUpRequestDTO topUpRequestDTO) {
        TopUpResponseDTO response = walletService.topUpWallet(topUpRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Wallet top-up successful", response));
    }
}
