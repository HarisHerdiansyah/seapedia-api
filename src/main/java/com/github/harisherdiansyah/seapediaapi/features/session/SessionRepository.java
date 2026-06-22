package com.github.harisherdiansyah.seapediaapi.features.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    Optional<SessionEntity> findSessionEntityById(UUID id);
    void deleteAllByExpiresAtBefore(java.time.OffsetDateTime dateTime);
}
