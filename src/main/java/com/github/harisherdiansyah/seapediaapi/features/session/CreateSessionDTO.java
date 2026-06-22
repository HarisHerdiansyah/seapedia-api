package com.github.harisherdiansyah.seapediaapi.features.session;

import java.util.UUID;

/**
 * @param jti well known as id in entity
 */
public record CreateSessionDTO(UUID jti, UUID userId, String deviceInfo, String ipAddress, ActiveRole activeRole) {
}
