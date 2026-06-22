package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NonAdminRoleRequestDTO {
    @NotNull(message = "Selected role can't be null.")
    private ActiveRole activeRole;
}
