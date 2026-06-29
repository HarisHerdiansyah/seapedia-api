package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.products.OrderStrategy;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductData;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductResponseDTO;
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
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PreAuthorize("hasRole('NON_ADMIN') and hasAuthority('REGISTER_STORE')")
    @PostMapping("")
    public ResponseEntity<?> registerStore(@Valid @RequestBody StoreRegisterRequestDTO storeRegisterRequestDTO) {
        storeService.registerStore(storeRegisterRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Store registered.", null));
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyStoreData() {
        StoreMeResponseDTO storeMeResponseDTO = storeService.getMyStoreData(SecurityUtil.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Data store retrieved.", storeMeResponseDTO));
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/products")
    public ResponseEntity<?> getStoreProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID category,
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
        ProductResponseDTO<StoreProductData> response = storeService.getAllStoreProducts(pageable, category, minPrice, maxPrice);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product Retrieved", response));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getStoreProductById(@PathVariable UUID id) {
        StoreProductDetailData response = storeService.getStoreProductById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Detail Retrieved", response));
    }

    @GetMapping("/{storeId}/categories")
    public ResponseEntity<?> getAllCategoriesByStoreId(@PathVariable UUID storeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Categories Retrieved", storeService.getAllCategoriesByStoreId(storeId)));
    }

    @GetMapping("/{storeId}/profile")
    public ResponseEntity<?> getStoreProfileById(@PathVariable UUID storeId) {
        StoreProfileResponseDTO response = storeService.getStoreProfileById(storeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Store Profile Retrieved", response));
    }

    @GetMapping("/{storeId}/catalog")
    public ResponseEntity<?> getStoreProductsByStoreId(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID category) {
        Pageable pageable = PageRequest.of(page, size);
        ProductResponseDTO<ProductData> response = storeService.getAllStoreProductsByStoreId(storeId, pageable, category);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product Retrieved", response));
    }
}
