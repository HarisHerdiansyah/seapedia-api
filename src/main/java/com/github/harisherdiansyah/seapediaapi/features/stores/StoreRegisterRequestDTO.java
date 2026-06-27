package com.github.harisherdiansyah.seapediaapi.features.stores;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class StoreRegisterRequestDTO {
    @NotBlank(message = "User identifier cannot be empty.")
    private UUID userId;

    @NotBlank(message = "Store name cannot be empty.")
    @Size(min = 10, max = 50, message = "Store name must be 10-50 characters.")
    private String storeName;

    @NotBlank(message = "Location cannot be empty.")
    private String location;
}
