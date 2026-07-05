package com.github.harisherdiansyah.seapediaapi.features.delivery;

import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryMethodResponseDTO(UUID id, DeliveryMethod deliveryMethod, BigDecimal price) {
}
