package com.github.harisherdiansyah.seapediaapi.core.utils;

import com.github.harisherdiansyah.seapediaapi.features.authentication.UserPrincipalEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtil {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipalEntity) {
            return ((UserPrincipalEntity) principal).getUserId();
        }

        return null;
    }
}
