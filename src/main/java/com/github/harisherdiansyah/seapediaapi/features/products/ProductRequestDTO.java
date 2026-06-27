package com.github.harisherdiansyah.seapediaapi.features.products;
    
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequestDTO {

    @NotNull(message = "Category ID cannot be empty.")
    private UUID categoryId;

    @NotBlank(message = "Product name cannot be empty.")
    @Size(min = 10, max = 50, message = "Product name must be between 10 and 50 characters.")
    private String name;

    @NotNull(message = "Price cannot be empty.")
    @Min(value = 0, message = "Price cannot be negative.")
    private BigDecimal price;

    @NotNull(message = "Stock cannot be empty.")
    @Min(value = 0, message = "Stock cannot be negative.")
    private Integer stock;

    @NotBlank(message = "Description cannot be empty.")
    @Size(min = 30, message = "Description must be at least 30 characters.")
    private String description;
}
