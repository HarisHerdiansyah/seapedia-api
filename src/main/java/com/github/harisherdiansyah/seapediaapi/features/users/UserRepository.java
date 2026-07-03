package com.github.harisherdiansyah.seapediaapi.features.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsUserEntityByUsername(String username);
    boolean existsUserEntityByEmail(String email);
    Optional<UserEntity> findUserEntityByEmail(String email);
    Optional<UserEntity> findUserEntityById(UUID id);
}
