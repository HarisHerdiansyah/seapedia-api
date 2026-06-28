package com.github.harisherdiansyah.seapediaapi.features.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    Optional<SessionEntity> findSessionEntityById(UUID id);

    @Query("SELECT u.id as id, u.email as email, u.role as role, s.activeRole as activeRole FROM UserEntity u JOIN SessionEntity s ON u.id = s.userId WHERE s.id = :id")
    Optional<UserSessionInfo> findUserSessionInfoBySessionId(UUID id);

    void deleteAllByExpiresAtBefore(java.time.OffsetDateTime dateTime);
}
