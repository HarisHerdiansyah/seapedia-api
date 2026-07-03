package com.github.harisherdiansyah.seapediaapi.features.carts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteItemRequestDTO {
    @NotNull(message = "Cart item data cannot be null.")
    private UUID cartItemId;

    @NotNull(message = "Cart data cannot be null.")
    private UUID cartId;
}
