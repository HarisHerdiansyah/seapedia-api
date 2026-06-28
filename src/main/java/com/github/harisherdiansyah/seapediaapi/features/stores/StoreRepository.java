package com.github.harisherdiansyah.seapediaapi.features.stores;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
    boolean existsByUserId(UUID userId);

    Optional<StoreEntity> findByUserId(UUID userId);

    @Query("SELECT s.storeName AS storeName, s.location AS location, u.username AS username, u.email AS email, s.createdAt AS joinedAt " +
            "FROM StoreEntity s JOIN s.user u WHERE u.id = :userId")
    Optional<StoreMeResponseDTO> findStoreMeResponseByUserId(UUID userId);

    @Query("SELECT new com.github.harisherdiansyah.seapediaapi.features.stores.StoreProductData(p.id, p.name, c.name, p.price, p.stock, p.updatedAt) " +
            "FROM ProductEntity p JOIN p.store s JOIN p.category c " +
            "WHERE s.id = :storeId AND " +
            "(:category IS NULL OR c.id = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Slice<StoreProductData> findProductSlicesByStoreId(
            Pageable pageable,
            @Param("storeId") UUID storeId,
            @Param("category") UUID category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT new com.github.harisherdiansyah.seapediaapi.features.stores.StoreProductDetailData(p.id, p.name, c.id, p.price, p.stock, p.description, p.imageUrl) " +
            "FROM ProductEntity p JOIN p.category c " +
            "WHERE p.id = :id")
    Optional<StoreProductDetailData> findStoreProductDetailById(@Param("id") UUID id);
}
