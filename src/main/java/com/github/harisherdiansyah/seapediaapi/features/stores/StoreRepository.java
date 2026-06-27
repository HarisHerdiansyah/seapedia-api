package com.github.harisherdiansyah.seapediaapi.features.stores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
    boolean existsByUser_Id(UUID userId);

    Optional<StoreEntity> findByUser_Id(UUID userId);
}
