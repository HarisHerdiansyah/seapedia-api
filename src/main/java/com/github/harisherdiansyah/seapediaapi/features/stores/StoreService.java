package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;

    public boolean isStoreExistByUserId(UUID userId) {
        return storeRepository.existsByUser_Id(userId);
    }

    public StoreEntity getStoreByUserId(UUID userId) {
        return storeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NotFoundException("Store not found for user ID: " + userId));
    }

    public void registerStore(StoreRegisterRequestDTO requestDTO) {
        UserEntity user = userService.getUserById(requestDTO.getUserId());

        StoreEntity storeEntity = StoreEntity.builder()
                .user(user)
                .storeName(requestDTO.getStoreName())
                .location(requestDTO.getLocation())
                .build();

        storeRepository.save(storeEntity);
    }
}
