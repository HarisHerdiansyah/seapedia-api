package com.github.harisherdiansyah.seapediaapi.features.session;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public void createSession(CreateSessionDTO sessionDTO) {
        SessionEntity.SessionEntityBuilder builder = SessionEntity
                .builder()
                .id(sessionDTO.jti())
                .userId(sessionDTO.userId())
                .ipAddress(sessionDTO.ipAddress())
                .deviceInfo(sessionDTO.deviceInfo())
                .activeRole(sessionDTO.activeRole())
                .expiresAt(OffsetDateTime.now().plusDays(7));

        sessionRepository.save(builder.build());
    }

    public void updateActiveRoleSession(UUID jti, ActiveRole activeRole) {
        SessionEntity sessionObject = sessionRepository.findSessionEntityById(jti)
                .orElseThrow(() -> new NotFoundException(""));

        sessionObject.setActiveRole(activeRole);
        sessionRepository.save(sessionObject);
    }

    public boolean isSessionExist(UUID jti) {
        return sessionRepository.existsById(jti);
    }

    public void deleteSession(UUID jti) {
        sessionRepository.deleteById(jti);
    }

    public void cleanUpExpiredSession() {}
}
