package com.github.harisherdiansyah.seapediaapi.features.stores;

import java.time.OffsetDateTime;

public record StoreMeResponseDTO(
        String storeName,
        String location,
        String username,
        String email,
        OffsetDateTime joinedAt
) {
}
