package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddressEntity, UUID> {

    List<UserAddressEntity> findAllByUserId(UUID userId);

    Optional<UserAddressEntity> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserId(UUID userId);

    long countByUserId(UUID userId);

    @Modifying
    @Query("UPDATE UserAddressEntity a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultByUserId(@Param("userId") UUID userId);
}
