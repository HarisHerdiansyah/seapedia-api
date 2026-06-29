package com.github.harisherdiansyah.seapediaapi.features.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to login")
public class LoginRequestDTO {
    @Schema(description = "Email Address", example = "user@example.com")
    @NotBlank(message = "Email cannot be empty.")
    @Email
    private String email;

    @Schema(description = "Password", example = "Password123!")
    @NotBlank(message = "Password can't be empty.")
    private String password;
}
