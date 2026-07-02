package com.github.harisherdiansyah.seapediaapi.features.carts;

import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findCartEntityByUserId(UUID userId);
    boolean existsByUserId(UUID userId);

    UUID user(UserEntity user);
}
