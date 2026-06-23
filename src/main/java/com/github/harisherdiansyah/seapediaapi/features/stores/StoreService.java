package com.github.harisherdiansyah.seapediaapi.features.stores;

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
}
