package com.github.harisherdiansyah.seapediaapi.features.carts;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @GetMapping("")
    public ResponseEntity<?> getAllUserCartItems() {
        GetCartResponseDTO response = cartService.getAllUserCartItems();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Successfully retrieved all user cart items", response));
    }

    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @PostMapping("")
    public ResponseEntity<?> mutateCartItem(@Valid @RequestBody MutateCartRequestDTO mutateCartRequestDTO) {
        MutateCartResponseDTO response = cartService.mutateCart(mutateCartRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Successfully mutated cart item", response));
    }

    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @DeleteMapping("")
    public ResponseEntity<?> deleteCartItem(@Valid @RequestBody DeleteItemRequestDTO deleteItemRequestDTO) {
        cartService.deleteCartItem(deleteItemRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Successfully deleted cart item", null));
    }

    @PreAuthorize("hasRole('BUYER') and hasAuthority('MANAGE_CART')")
    @PostMapping("/override")
    public ResponseEntity<?> overrideDataCart(@Valid @RequestBody MutateCartRequestDTO mutateCartRequestDTO) {
        MutateCartResponseDTO response = cartService.overrideDataCart(mutateCartRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Successfully reset cart item", response));
    }
}


//{
//        "statusCode": 404,
//        "success": false,
//        "message": "User is not register their store yet.",
//        "data": null,
//        "timestamp": "2026-07-03T06:07:49.48681995"
//        }
//