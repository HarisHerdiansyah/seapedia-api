package com.github.harisherdiansyah.seapediaapi.features.carts;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("")
    public ResponseEntity<?> getAllUserCartItems() {
        GetCartResponseDTO response = cartService.getAllUserCartItems();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Successfully retrieved all user cart items", response));
    }

    @PostMapping("")
    public ResponseEntity<?> mutateCartItem(
            @Valid @RequestBody MutateCartRequestDTO mutateCartRequestDTO) {
        MutateCartResponseDTO response = cartService.mutateCart(mutateCartRequestDTO);

        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Failed to mutate cart item", null));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(HttpStatus.CREATED.value(), "Successfully mutated cart item", response));
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteCartItem(
            @Valid @RequestBody DeleteItemRequestDTO deleteItemRequestDTO) {
        cartService.deleteCartItem(deleteItemRequestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Successfully deleted cart item", null));
    }
}
