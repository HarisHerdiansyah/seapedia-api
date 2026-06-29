package com.github.harisherdiansyah.seapediaapi.features.products;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Seapedia Product Management")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Get all products", description = "Retrieves a list of products with support for pagination, search, and filtering.")
    @GetMapping("")
    public ResponseEntity<?> getAllProducts(
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Data size per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Product name search keyword") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by category UUID") @RequestParam(required = false) UUID category,
            @Parameter(description = "Minimum price filter") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price filter") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Sorting strategy (NEWEST, OLDEST, PRICE_ASC, PRICE_DESC)") @RequestParam(defaultValue = "NEWEST") String order
    ) {
        OrderStrategy orderStrategy;
        try {
            orderStrategy = OrderStrategy.valueOf(order.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            orderStrategy = OrderStrategy.NEWEST;
        }

        Sort sort = switch (orderStrategy) {
            case OrderStrategy.NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OrderStrategy.OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case OrderStrategy.PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price");
            case OrderStrategy.PRICE_DESC -> Sort.by(Sort.Direction.DESC, "price");
        };
        Pageable pageable = PageRequest.of(page, size, sort);
        ProductResponseDTO<ProductData> response = productService.getAllProducts(pageable, search, category, minPrice, maxPrice);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product Retrieved", response));
    }

    @Operation(summary = "Get product detail", description = "Retrieves detailed product information by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@Parameter(description = "Product UUID") @PathVariable UUID id) {
        ProductDetailData response = productService.getProductById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Detail Retrieved", response));
    }

    @Operation(summary = "Add new product", description = "Creates a new product entry. Requires SELLER role and MANAGE_PRODUCT authority.")
    @PreAuthorize("hasRole('SELLER') and hasAuthority('MANAGE_PRODUCT')")
    @PostMapping("")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        ProductDetailData product = productService.createProduct(productRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Product created", product));
    }

    @Operation(summary = "Update product", description = "Updates existing product data. Requires SELLER role and MANAGE_PRODUCT authority.")
    @PreAuthorize("hasRole('SELLER') and hasAuthority('MANAGE_PRODUCT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "Product UUID") @PathVariable UUID id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductDetailData product = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Product updated", product));
    }

    @Operation(summary = "Delete product", description = "Deletes product by ID. Requires SELLER role and MANAGE_PRODUCT authority.")
    @PreAuthorize("hasRole('SELLER') and hasAuthority('MANAGE_PRODUCT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@Parameter(description = "Product UUID") @PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product deleted", null));
    }
}
