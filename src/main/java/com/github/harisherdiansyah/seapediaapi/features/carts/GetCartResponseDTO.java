package com.github.harisherdiansyah.seapediaapi.features.carts;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record GetCartResponseDTO(UUID storeId, String storeName, BigDecimal totalPrice, List<CartItemsData> items) {
}
