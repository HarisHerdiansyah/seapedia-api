package com.github.harisherdiansyah.seapediaapi.features.carts;

import java.math.BigDecimal;
import java.util.UUID;

public record MutateCartResponseDTO(UUID productId, String productName, BigDecimal subtotal, Integer quantity) {
}
