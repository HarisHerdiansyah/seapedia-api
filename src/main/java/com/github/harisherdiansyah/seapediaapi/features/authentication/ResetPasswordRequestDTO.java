package com.github.harisherdiansyah.seapediaapi.features.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotBlank(message = "Email can't by empty.")
    @Email
    private String email;

    @NotBlank(message = "New Password can't be empty.")
    @Size(min = 8, max = 16, message = "Password must be around 8-16 characters.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).*$",
            message = "Password at least have one number, one special characters, and one capital letter."
    )
    private String newPassword;
}
