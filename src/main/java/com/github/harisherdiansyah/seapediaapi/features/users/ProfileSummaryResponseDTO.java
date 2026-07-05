package com.github.harisherdiansyah.seapediaapi.features.users;

import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProfileSummaryResponseDTO(
        UUID userId, String username, String email,
        ActiveRole activeRole, List<ActiveRole> allowedAs,
        UUID walletId, BigDecimal walletBalance,
        UUID storeId, String storeName, String storeLocation
) {
}
