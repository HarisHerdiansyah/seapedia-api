package com.github.harisherdiansyah.seapediaapi.features.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotBlank(message = "Email cannot be empty.")
    @Email
    private String email;

    @NotBlank(message = "New password cannot be empty.")
    @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).*$",
            message = "Password must contain at least one uppercase letter, one number, and one special character."
    )
    private String newPassword;
}
