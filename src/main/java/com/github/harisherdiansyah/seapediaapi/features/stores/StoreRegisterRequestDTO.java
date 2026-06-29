package com.github.harisherdiansyah.seapediaapi.features.stores;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Request for new store registration")
public class StoreRegisterRequestDTO {
    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "User identifier cannot be empty.")
    private UUID userId;

    @Schema(description = "Store Name", example = "Bahari Fish Store")
    @NotBlank(message = "Store name cannot be empty.")
    @Size(min = 10, max = 50, message = "Store name must be 10-50 characters.")
    private String storeName;

    @Schema(description = "Store Location", example = "North Jakarta")
    @NotBlank(message = "Location cannot be empty.")
    private String location;
}
