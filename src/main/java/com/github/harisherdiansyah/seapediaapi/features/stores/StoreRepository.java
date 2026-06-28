package com.github.harisherdiansyah.seapediaapi.features.stores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
    boolean existsByUserId(UUID userId);

    Optional<StoreEntity> findByUserId(UUID userId);

    @Query("SELECT s.storeName AS storeName, s.location AS location, u.username AS username, u.email AS email, s.createdAt AS joinedAt " +
            "FROM StoreEntity s JOIN s.user u WHERE u.id = :userId")
    Optional<StoreMeResponseDTO> findStoreMeResponseByUserId(UUID userId);
}
