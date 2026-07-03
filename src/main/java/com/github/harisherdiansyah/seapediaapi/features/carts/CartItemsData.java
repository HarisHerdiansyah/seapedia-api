package com.github.harisherdiansyah.seapediaapi.features.carts;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemsData(UUID cartId, UUID itemId, UUID productId, String productName, String productImage, Integer quantity, BigDecimal price, BigDecimal subtotal) {
}
