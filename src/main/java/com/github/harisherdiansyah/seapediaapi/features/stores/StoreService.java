package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.exception.BadRequestException;
import com.github.harisherdiansyah.seapediaapi.core.exception.DuplicateDataException;
import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.categories.CategoryResponseDTO;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductData;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductResponseDTO;
import com.github.harisherdiansyah.seapediaapi.features.products.ProductService;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserService userService;

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

    public ProductResponseDTO<StoreProductData> getAllStoreProducts(Pageable pageable, UUID category, BigDecimal minPrice, BigDecimal maxPrice) {
        StoreEntity storeEntity = getStoreByUserId(SecurityUtil.getCurrentUserId());
        Slice<StoreProductData> productSlice = storeRepository.findProductSlicesByStoreId(pageable, storeEntity.getId(), category, minPrice, maxPrice);
        int pageNumber = productSlice.getNumber();
        boolean hasNext = productSlice.hasNext();
        List<StoreProductData> storeProductDataList = productSlice.getContent();

        return new ProductResponseDTO<>(pageNumber, hasNext, storeProductDataList);
    }

    public StoreProductDetailData getStoreProductById(UUID productId) {
        return storeRepository.findStoreProductDetailById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found for ID: " + productId));
    }

    public List<CategoryResponseDTO> getAllCategoriesByStoreId(UUID storeId) {
        return storeRepository.findAllCategoriesByStoreId(storeId);
    }

    public StoreProfileResponseDTO getStoreProfileById(UUID storeId) {
        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store not found for ID: " + storeId));
        return new StoreProfileResponseDTO(
                storeEntity.getId(),
                storeEntity.getStoreName(),
                storeEntity.getUser().getEmail(),
                storeEntity.getLocation()
        );
    }

    public ProductResponseDTO<ProductData> getAllStoreProductsByStoreId(UUID storeId, Pageable pageable, UUID category) {
        Slice<ProductData> productSlice = storeRepository.findCatalogSlicesByStoreId(pageable, storeId, category);
        int pageNumber = productSlice.getNumber();
        boolean hasNext = productSlice.hasNext();
        List<ProductData> storeProductDataList = productSlice.getContent();

        return new ProductResponseDTO<>(pageNumber, hasNext, storeProductDataList);
    }
}
