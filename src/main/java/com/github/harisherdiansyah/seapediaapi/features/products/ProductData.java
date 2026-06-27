package com.github.harisherdiansyah.seapediaapi.features.products;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ProductData(UUID id,
                          String name,
                          String category,
                          BigDecimal price,
                          String imageUrl,
                          String location,
                          OffsetDateTime updatedAt) {
}
