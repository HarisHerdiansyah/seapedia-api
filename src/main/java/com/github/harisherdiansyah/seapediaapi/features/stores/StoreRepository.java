package com.github.harisherdiansyah.seapediaapi.features.stores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
    boolean existsByUserId(UUID userId);

    Optional<StoreEntity> findByUserId(UUID userId);
}
