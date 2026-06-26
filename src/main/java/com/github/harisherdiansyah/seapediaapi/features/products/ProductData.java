package com.github.harisherdiansyah.seapediaapi.features.products;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductData(UUID id,
                          String name,
                          BigDecimal price,
                          Integer stock,
                          String imageUrl,
                          String storeName) {
}
