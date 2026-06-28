package com.github.harisherdiansyah.seapediaapi.features.stores;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record StoreProductData(
        UUID id,
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        OffsetDateTime updatedAt
) {
}
