package com.github.harisherdiansyah.seapediaapi.features.products;

import java.util.List;

public record ProductResponseDTO(int pageNumber, boolean hasNext, List<ProductData> productData) {
}
