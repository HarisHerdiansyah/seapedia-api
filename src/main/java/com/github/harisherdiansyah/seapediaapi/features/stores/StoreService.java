package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.exception.BadRequestException;
import com.github.harisherdiansyah.seapediaapi.core.exception.DuplicateDataException;
import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductResponseDTO;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductService;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final ProductService productService;

    public boolean isStoreExistByUserId(UUID userId) {
        return storeRepository.existsByUserId(userId);
    }

    public StoreEntity getStoreByUserId(UUID userId) {
        return storeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Store not found for user ID: " + userId));
    }

    public StoreMeResponseDTO getMyStoreData(UUID userId) {
        return storeRepository.findStoreMeResponseByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Store not found for user ID: " + userId));
    }

    public ProductResponseDTO getStoreProducts(UUID userId, Pageable pageable, UUID category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productService.getStoreProductsByUserId(userId, pageable, category, minPrice, maxPrice);
    }

    public void registerStore(StoreRegisterRequestDTO requestDTO) {
        boolean isUserSeller = isStoreExistByUserId(requestDTO.getUserId());
        if (isUserSeller) {
            throw new BadRequestException("User already registered as a seller.");
        }

        UserEntity user = userService.getUserById(requestDTO.getUserId());

        StoreEntity storeEntity = StoreEntity.builder()
                .user(user)
                .storeName(requestDTO.getStoreName())
                .location(requestDTO.getLocation())
                .build();

        storeRepository.save(storeEntity);
    }
}
