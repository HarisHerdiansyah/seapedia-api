package com.github.harisherdiansyah.seapediaapi.features.products;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "NEWEST") String order
    ) {
        OrderStrategy orderStrategy = OrderStrategy.valueOf(order.toUpperCase());
        Sort sort = switch (orderStrategy) {
            case OrderStrategy.NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OrderStrategy.OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case OrderStrategy.PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price");
            case OrderStrategy.PRICE_DESC -> Sort.by(Sort.Direction.DESC, "price");
        };
        Pageable pageable = PageRequest.of(page, size, sort);
        ProductResponseDTO response = productService.getAllProducts(pageable, category, minPrice, maxPrice);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product Retrieved", response));
    }
}
