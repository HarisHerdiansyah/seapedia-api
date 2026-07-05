package com.github.harisherdiansyah.seapediaapi.features.wallets;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "User Wallet Management")
public class WalletController {
    private final WalletService walletService;

    @Operation(summary = "Get wallet summary", description = "Retrieves the authenticated user's wallet balance and paginated transaction history. Requires MANAGE_WALLET authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wallet summary retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires MANAGE_WALLET authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('MANAGE_WALLET')")
    @GetMapping("/summary")
    public ResponseEntity<?> getWalletSummary(
            @Parameter(description = "Page number (starting from 0)") @RequestParam int page,
            @Parameter(description = "Number of transaction records per page") @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        WalletSummaryResponseDTO summary = walletService.getWalletSummary(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Wallet summary retrieved successfully", summary));
    }

    @Operation(summary = "Top up wallet", description = "Adds balance to the authenticated user's wallet. Requires MANAGE_WALLET authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Wallet top-up successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires MANAGE_WALLET authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('MANAGE_WALLET')")
    @PostMapping("/top-up")
    public ResponseEntity<?> topUpWallet(@Valid @RequestBody TopUpRequestDTO topUpRequestDTO) {
        TopUpResponseDTO response = walletService.topUpWallet(topUpRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Wallet top-up successful", response));
    }
}
