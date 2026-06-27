package com.github.harisherdiansyah.seapediaapi.features.products;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT new com.github.harisherdiansyah.seapediaapi.features.products.ProductData(p.id, p.name, c.name, p.price, p.imageUrl, s.location, p.updatedAt) " +
            "FROM ProductEntity p JOIN p.store s JOIN p.category c " +
            "WHERE (:category IS NULL OR c.id = :category) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Slice<ProductData> findProductSlices(
            Pageable pageable,
            @Param("category") UUID category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
