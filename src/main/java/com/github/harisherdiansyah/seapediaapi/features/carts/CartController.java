package com.github.harisherdiansyah.seapediaapi.features.carts;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping Cart Management")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Get all cart items", description = "Retrieves all items in the authenticated buyer's cart. Requires BUYER role and MANAGE_CART authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart items retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role with MANAGE_CART authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @GetMapping("")
    public ResponseEntity<?> getAllUserCartItems() {
        GetCartResponseDTO response = cartService.getAllUserCartItems();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Successfully retrieved all user cart items", response));
    }

    @Operation(summary = "Add or update cart item", description = "Adds a new item or updates the quantity of an existing item in the cart. Requires BUYER role and MANAGE_CART authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cart item mutated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role with MANAGE_CART authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @PostMapping("")
    public ResponseEntity<?> mutateCartItem(@Valid @RequestBody MutateCartRequestDTO mutateCartRequestDTO) {
        MutateCartResponseDTO response = cartService.mutateCart(mutateCartRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Successfully mutated cart item", response));
    }

    @Operation(summary = "Delete cart item", description = "Removes a specific item from the cart. Requires BUYER role and MANAGE_CART authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart item deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role with MANAGE_CART authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - cart item does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @DeleteMapping("")
    public ResponseEntity<?> deleteCartItem(@Valid @RequestBody DeleteItemRequestDTO deleteItemRequestDTO) {
        cartService.deleteCartItem(deleteItemRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Successfully deleted cart item", null));
    }

    @Operation(summary = "Override (reset) cart item", description = "Replaces the cart item quantity with a new value, overriding the current quantity. Requires BUYER role and MANAGE_CART authority.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cart item overridden successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role with MANAGE_CART authority"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @PostMapping("/override")
    public ResponseEntity<?> overrideDataCart(@Valid @RequestBody MutateCartRequestDTO mutateCartRequestDTO) {
        MutateCartResponseDTO response = cartService.overrideDataCart(mutateCartRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Successfully reset cart item", response));
    }
}