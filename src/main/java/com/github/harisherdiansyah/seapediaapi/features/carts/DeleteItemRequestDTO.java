package com.github.harisherdiansyah.seapediaapi.features.carts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteItemRequestDTO {
    @NotNull(message = "Items data cannot be null.")
    private UUID itemsId;

    @NotNull(message = "Flag cannot be null.")
    private boolean isLastProduct;
}
