package com.github.harisherdiansyah.seapediaapi.features.session;

import java.util.UUID;

public interface UserSessionInfo {
    UUID getId();
    String getEmail();
    com.github.harisherdiansyah.seapediaapi.features.users.UserRole getRole();
    ActiveRole getActiveRole();
}
