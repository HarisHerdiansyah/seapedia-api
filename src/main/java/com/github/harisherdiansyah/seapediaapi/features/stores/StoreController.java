package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.products.OrderStrategy;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductData;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "Store and Store Catalog Management")
public class StoreController {
    private final StoreService storeService;

    @Operation(summary = "Register store", description = "Registers a new store for the user. Requires REGISTER_STORE authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Store registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires REGISTER_STORE authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - store already registered for this user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('REGISTER_STORE')")
    @PostMapping("")
    public ResponseEntity<?> registerStore(
            @Valid @RequestBody StoreRegisterRequestDTO storeRegisterRequestDTO,
            @CookieValue(name = "refreshToken", defaultValue = "") String rt
    ) {
        Map<String, Object> result = storeService.registerStore(storeRegisterRequestDTO, rt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Store registered.", result));
    }

    @Operation(summary = "My store data", description = "Retrieves store data belonging to the currently logged-in user. Requires SELLER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store data retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires SELLER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - store not yet registered"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/me")
    public ResponseEntity<?> getMyStoreData() {
        StoreMeResponseDTO storeMeResponseDTO = storeService.getMyStoreData(SecurityUtil.getCurrentUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Data store retrieved.", storeMeResponseDTO));
    }

    @Operation(summary = "My store products", description = "Retrieves the list of products in the user's own store. Requires SELLER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store products retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires SELLER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/products")
    public ResponseEntity<?> getStoreProducts(
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Product name search keyword") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by category UUID") @RequestParam(required = false) UUID category,
            @Parameter(description = "Minimum price filter") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price filter") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Sorting strategy (NEWEST, OLDEST, PRICE_ASC, PRICE_DESC)") @RequestParam(defaultValue = "NEWEST") String order
    ) {
        OrderStrategy orderStrategy = OrderStrategy.valueOf(order.toUpperCase());
        Sort sort = switch (orderStrategy) {
            case OrderStrategy.NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OrderStrategy.OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case OrderStrategy.PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price");
            case OrderStrategy.PRICE_DESC -> Sort.by(Sort.Direction.DESC, "price");
        };
        Pageable pageable = PageRequest.of(page, size, sort);
        ProductResponseDTO<StoreProductData> response = storeService.getAllStoreProducts(pageable, search, category, minPrice, maxPrice);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product Retrieved", response));
    }

    @Operation(summary = "Store product detail", description = "Retrieves specific product details in the store.")
    @SecurityRequirement(name = "")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store product detail retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - product does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getStoreProductById(
            @Parameter(description = "Product UUID") @PathVariable UUID id) {
        StoreProductDetailData response = storeService.getStoreProductById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Detail Retrieved", response));
    }

    @Operation(summary = "Store categories", description = "Retrieves all product categories available in a specific store.")
    @SecurityRequirement(name = "")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store categories retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - store does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{storeId}/categories")
    public ResponseEntity<?> getAllCategoriesByStoreId(
            @Parameter(description = "Store UUID") @PathVariable UUID storeId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Categories Retrieved", storeService.getAllCategoriesByStoreId(storeId)));
    }

    @Operation(summary = "Store profile", description = "Retrieves the public profile of a store.")
    @SecurityRequirement(name = "")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store profile retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - store does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{storeId}/profile")
    public ResponseEntity<?> getStoreProfileById(
            @Parameter(description = "Store UUID") @PathVariable UUID storeId) {
        StoreProfileResponseDTO response = storeService.getStoreProfileById(storeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Store Profile Retrieved", response));
    }

    @Operation(summary = "Store product catalog", description = "Retrieves the product catalog from a specific store.")
    @SecurityRequirement(name = "")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Store product catalog retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - store does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{storeId}/catalog")
    public ResponseEntity<?> getStoreProductsByStoreId(
            @Parameter(description = "Store UUID") @PathVariable UUID storeId,
            @Parameter(description = "Page number (starting from 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by category UUID") @RequestParam(required = false) UUID category) {
        Pageable pageable = PageRequest.of(page, size);
        ProductResponseDTO<ProductData> response = storeService.getAllStoreProductsByStoreId(storeId, pageable, category);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Product Retrieved", response));
    }
}
