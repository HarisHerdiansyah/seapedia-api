package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.features.users.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Username can't be empty.")
    @Size(min = 8, max = 50, message = "Username must be at least 8 characters.")
    private String username;

    @NotBlank(message = "Email can't by empty.")
    @Email
    private String email;

    @NotBlank(message = "Password can't be empty.")
    @Size(min = 8, max = 16, message = "Password must be around 8-16 characters.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).*$",
            message = "Password at least have one number, one special characters, and one capital letter."
    )
    private String password;

    @NotNull(message = "Role can't be empty, choose ADMIN or NON_ADMIN.")
    private UserRole role;
}
