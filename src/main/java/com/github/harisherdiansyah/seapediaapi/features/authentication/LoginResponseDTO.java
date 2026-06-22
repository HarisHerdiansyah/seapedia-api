package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.features.users.UserRole;
import lombok.Data;

@Data
public class LoginResponseDTO {
    private String accessToken;
    private UserObject userData;

    @Data
    private static class UserObject {
        private String id;
        private String username;
        private String email;
        private UserRole role;
    }
}
