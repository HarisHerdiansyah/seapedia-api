package com.github.harisherdiansyah.seapediaapi.features.products;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailData(
        UUID id,
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        String description,
        String imageUrl,
        String location,
        String storeName
) {
}
