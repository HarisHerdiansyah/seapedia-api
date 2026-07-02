package com.github.harisherdiansyah.seapediaapi.features.carts;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemsData(UUID productId, String productName, String productImage, Integer quantity, BigDecimal subtotal) {
}
