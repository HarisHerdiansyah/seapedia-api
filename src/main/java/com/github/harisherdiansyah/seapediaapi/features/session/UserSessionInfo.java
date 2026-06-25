package com.github.harisherdiansyah.seapediaapi.features.session;

import java.util.UUID;

public interface UserSessionInfo {
    UUID getId();
    String getEmail();
    ActiveRole getActiveRole();
}
