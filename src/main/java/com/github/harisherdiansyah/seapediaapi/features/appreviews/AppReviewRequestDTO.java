package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to submit an app review")
public class AppReviewRequestDTO {
    @Schema(description = "Reviewer Name", example = "John Doe")
    @NotNull(message = "App reviewer cannot be null.")
    private String reviewer;

    @Schema(description = "App Rating (1-5)", example = "4.5")
    @NotNull(message = "App rating cannot be empty.")
    private BigDecimal rating;

    @Schema(description = "Review Content", example = "This app is very helpful for fishermen!")
    @NotBlank(message = "App review content cannot be empty.")
    private String content;
}
