package com.github.harisherdiansyah.seapediaapi.features.carts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MutateCartRequestDTO {
    @NotNull(message = "Store cannot be empty.")
    private UUID storeId;

    @NotNull(message = "Product ID cannot be empty.")
    private UUID productId;

    @NotNull(message = "Product name cannot be empty.")
    private String productName;

    @NotNull(message = "Quantity cannot be empty.")
    @Min(value = 1, message = "Quantity must be greater than or equal to 1.")
    private Integer quantity;
}
