package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppReviewRequestDTO {
    @NotBlank(message = "App reviewer cannot be empty.")
    private String reviewer;

    @NotNull(message = "App rating cannot be empty.")
    private BigDecimal rating;

    @NotBlank(message = "App review content cannot be empty.")
    private String content;
}
