package com.github.harisherdiansyah.seapediaapi.features.stores;

import java.util.UUID;

public record StoreProfileResponseDTO(UUID id, String storeName, String ownerEmail, String location) {
}
