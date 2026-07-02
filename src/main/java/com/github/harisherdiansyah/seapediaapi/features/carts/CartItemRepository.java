package com.github.harisherdiansyah.seapediaapi.features.carts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemsEntity, UUID> {
    @Query("""
            SELECT new com.github.harisherdiansyah.seapediaapi.features.carts.CartItemsData(
                p.id,
                p.name,
                p.imageUrl,
                ci.quantity,
                p.price
            )
            FROM CartItemsEntity ci
            JOIN ci.product p
            WHERE ci.cart.id = :cartId
        """)
    List<CartItemQueryResult> findCartItemsByCartId(UUID cartId);
}
