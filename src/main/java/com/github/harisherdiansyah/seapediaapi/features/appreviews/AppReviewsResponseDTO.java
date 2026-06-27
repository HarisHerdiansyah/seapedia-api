package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import java.math.BigDecimal;
import java.util.UUID;

public record AppReviewsResponseDTO(UUID id, String reviewer, String content, BigDecimal rating) {
}
