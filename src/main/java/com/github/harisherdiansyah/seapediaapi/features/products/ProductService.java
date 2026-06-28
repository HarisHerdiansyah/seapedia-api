package com.github.harisherdiansyah.seapediaapi.features.products;

import com.github.harisherdiansyah.seapediaapi.core.exception.ForbiddenException;
import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.JwtUtility;
import com.github.harisherdiansyah.seapediaapi.features.categories.CategoryEntity;
import com.github.harisherdiansyah.seapediaapi.features.categories.CategoryService;
import com.github.harisherdiansyah.seapediaapi.features.session.SessionService;
import com.github.harisherdiansyah.seapediaapi.features.session.UserSessionInfo;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreEntity;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final JwtUtility jwtUtility;
    private final ProductRepository productRepository;
    private final SessionService sessionService;
    private final StoreService storeService;
    private final CategoryService categoryService;

    public ProductResponseDTO<ProductData> getAllProducts(Pageable pageable, UUID category, BigDecimal minPrice, BigDecimal maxPrice) {
        Slice<ProductData> productSlice = productRepository.findProductSlices(pageable, category, minPrice, maxPrice);
        int pageNumber = productSlice.getNumber();
        boolean hasNext = productSlice.hasNext();
        List<ProductData> productDataList = productSlice.getContent();

        return new ProductResponseDTO<>(pageNumber, hasNext, productDataList);
    }

    public ProductDetailData getProductById(UUID productId) {
        return productRepository.findProductDetailById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found for ID: " + productId));
    }

    public ProductDetailData createProduct(ProductRequestDTO productRequestDTO, String rt) {
        if (!StringUtils.hasText(rt)) {
            throw new ForbiddenException("Refresh token is missing.");
        }

        UUID currentTokenJti = UUID.fromString(jwtUtility.extractJti(rt));
        if (!sessionService.isSessionExist(currentTokenJti)) {
            throw new ForbiddenException("Session not found. Please re-login.");
        }

        UserSessionInfo userSessionInfo = sessionService.getUserSessionInfo(currentTokenJti);
        UUID userId = userSessionInfo.getId();
        StoreEntity storeEntity = storeService.getStoreByUserId(userId);
        CategoryEntity categoryEntity = categoryService.getCategoryById(productRequestDTO.getCategoryId());

        ProductEntity productEntity = ProductEntity.builder()
                .category(categoryEntity)
                .store(storeEntity)
                .name(productRequestDTO.getName())
                .price(productRequestDTO.getPrice())
                .stock(productRequestDTO.getStock())
                .description(productRequestDTO.getDescription())
                .imageUrl("")
                .build();

        productRepository.save(productEntity);

        return new ProductDetailData(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getCategory().getName(),
                productEntity.getPrice(),
                productEntity.getStock(),
                productEntity.getDescription(),
                productEntity.getImageUrl(),
                storeEntity.getLocation(),
                storeEntity.getStoreName()
        );
    }

    public ProductDetailData updateProduct(UUID productId, ProductRequestDTO productRequestDTO) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ForbiddenException("Product not found for ID: " + productId));
        CategoryEntity categoryEntity = categoryService.getCategoryById(productRequestDTO.getCategoryId());

        productEntity.setName(productRequestDTO.getName());
        productEntity.setPrice(productRequestDTO.getPrice());
        productEntity.setStock(productRequestDTO.getStock());
        productEntity.setDescription(productRequestDTO.getDescription());
        productEntity.setCategory(categoryEntity);

        productRepository.save(productEntity);

        return new ProductDetailData(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getCategory().getName(),
                productEntity.getPrice(),
                productEntity.getStock(),
                productEntity.getDescription(),
                productEntity.getImageUrl(),
                productEntity.getStore().getLocation(),
                productEntity.getStore().getStoreName()
        );
    }

    public void deleteProduct(UUID productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ForbiddenException("Product not found for ID: " + productId));
        productRepository.delete(productEntity);
    }
}
