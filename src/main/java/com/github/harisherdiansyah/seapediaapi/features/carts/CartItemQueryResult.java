package com.github.harisherdiansyah.seapediaapi.features.carts;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemQueryResult(UUID productId, String productName, String productImage, Integer quantity, BigDecimal price) {
}
