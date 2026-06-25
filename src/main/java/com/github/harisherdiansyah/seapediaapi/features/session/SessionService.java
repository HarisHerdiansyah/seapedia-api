package com.github.harisherdiansyah.seapediaapi.features.session;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public void createSession(CreateSessionDTO sessionDTO) {
        SessionEntity session = SessionEntity.builder()
                .id(sessionDTO.jti())
                .userId(sessionDTO.userId())
                .ipAddress(sessionDTO.ipAddress())
                .deviceInfo(sessionDTO.deviceInfo())
                .activeRole(sessionDTO.activeRole())
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .build();

        sessionRepository.save(session);
    }

    public void updateActiveRoleSession(UUID jti, ActiveRole activeRole) {
        SessionEntity session = sessionRepository.findSessionEntityById(jti)
                .orElseThrow(() -> new NotFoundException("Session not found."));

        session.setActiveRole(activeRole);
        sessionRepository.save(session);
    }

    public boolean isSessionExist(UUID jti) {
        return sessionRepository.existsById(jti);
    }

    public void deleteSession(UUID jti) {
        sessionRepository.deleteById(jti);
    }

    public UserSessionInfo getUserSessionInfo(UUID jti) {
        return sessionRepository.findUserSessionInfoBySessionId(jti)
                .orElseThrow(() -> new NotFoundException("Session not found."));
    }

    /**
     * Cleans up sessions that have passed their expiry time.
     * Runs every hour to keep the sessions table lean.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanUpExpiredSession() {
        sessionRepository.deleteAllByExpiresAtBefore(OffsetDateTime.now());
    }
}
