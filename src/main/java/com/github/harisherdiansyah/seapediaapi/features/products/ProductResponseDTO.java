package com.github.harisherdiansyah.seapediaapi.features.products;

import java.util.List;

public record ProductResponseDTO<T>(int pageNumber, boolean hasNext, List<T> productData) {
}
