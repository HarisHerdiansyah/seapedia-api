package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;

    public boolean isStoreExistByUserId(UUID userId) {
        return storeRepository.existsByUserId(userId);
    }

    public StoreEntity getStoreByUserId(UUID userId) {
        return storeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Store not found for user ID: " + userId));
    }
}
