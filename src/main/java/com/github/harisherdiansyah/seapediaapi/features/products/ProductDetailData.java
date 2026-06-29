package com.github.harisherdiansyah.seapediaapi.features.products;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailData(
        UUID id,
        String name,
        UUID categoryId,
        String category,
        BigDecimal price,
        Integer stock,
        String description,
        BigDecimal rating,
        String imageUrl,
        String location,
        UUID storeId,
        String storeName
) {
}
