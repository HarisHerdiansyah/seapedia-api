package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import com.github.harisherdiansyah.seapediaapi.features.users.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private UserObject userData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserObject {
        private UUID id;
        private String username;
        private String email;
        private UserRole role;
        private List<ActiveRole> allowedAs;
    }
}
