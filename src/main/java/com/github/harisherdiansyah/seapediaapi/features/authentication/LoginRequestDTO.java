package com.github.harisherdiansyah.seapediaapi.features.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Email cannot be empty.")
    @Email
    private String email;

    @NotBlank(message = "Password can't be empty.")
    private String password;
}
