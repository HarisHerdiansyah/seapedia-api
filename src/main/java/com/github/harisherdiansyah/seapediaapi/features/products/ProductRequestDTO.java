package com.github.harisherdiansyah.seapediaapi.features.products;
    
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Request to create or update a product")
public class ProductRequestDTO {

    @Schema(description = "Category ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Category ID cannot be empty.")
    private UUID categoryId;

    @Schema(description = "Product Name", example = "Fresh Salmon Fish")
    @NotBlank(message = "Product name cannot be empty.")
    @Size(min = 10, max = 50, message = "Product name must be between 10 and 50 characters.")
    private String name;

    @Schema(description = "Product Price", example = "150000")
    @NotNull(message = "Price cannot be empty.")
    @Min(value = 0, message = "Price cannot be negative.")
    private BigDecimal price;

    @Schema(description = "Product Stock", example = "100")
    @NotNull(message = "Stock cannot be empty.")
    @Min(value = 0, message = "Stock cannot be negative.")
    private Integer stock;

    @Schema(description = "Product Description", example = "Fresh salmon fish directly from local fishermen.")
    @NotBlank(message = "Description cannot be empty.")
    @Size(min = 30, message = "Description must be at least 30 characters.")
    private String description;

    @Schema(description = "Product Image URL", example = "https://example.com/salmon.jpg")
    private String imageUrl;
}
