package com.github.harisherdiansyah.seapediaapi.features.products;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponseDTO getAllProducts(Pageable pageable, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        Slice<ProductData> productSlice = productRepository.findProductSlices(pageable, category, minPrice, maxPrice);
        int pageNumber = productSlice.getNumber();
        boolean hasNext = productSlice.hasNext();
        List<ProductData> productDataList = productSlice.getContent();

        return new ProductResponseDTO(pageNumber, hasNext, productDataList);
    }
}
