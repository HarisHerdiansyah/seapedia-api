package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.features.users.UserRole;

import java.util.UUID;

public record RegisterResponseDTO(UUID id, String username, String email, UserRole role) {
}
