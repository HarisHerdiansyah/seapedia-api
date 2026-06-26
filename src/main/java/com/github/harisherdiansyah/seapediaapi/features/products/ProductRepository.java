package com.github.harisherdiansyah.seapediaapi.features.products;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Query("SELECT new com.github.harisherdiansyah.seapediaapi.features.products.ProductData(p.id, p.name, p.price, p.stock, p.imageUrl, s.storeName) " +
            "FROM ProductEntity p JOIN p.store s")
    Slice<ProductData> findProductSlices(Pageable pageable);
}
