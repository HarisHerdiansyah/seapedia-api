package com.github.harisherdiansyah.seapediaapi.features.stores;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreProductDetailData(
        UUID id,
        String name,
        UUID categoryId,
        BigDecimal price,
        Integer stock,
        String description,
        String imageUrl
) {
}
