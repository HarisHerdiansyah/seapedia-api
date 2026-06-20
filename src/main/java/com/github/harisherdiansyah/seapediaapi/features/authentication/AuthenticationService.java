package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.exception.DuplicateDataException;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        boolean isUserExist = userService.isUserExistByUsername(registerRequestDTO.getUsername());
        if (isUserExist) {
            throw new DuplicateDataException("User with username " + registerRequestDTO.getUsername() + " is already exist.");
        }

        boolean isEmailExist = userService.isUserExistByEmail(registerRequestDTO.getEmail());
        if (isEmailExist) {
            throw new DuplicateDataException("User with email " + registerRequestDTO.getEmail() + " is already exist.");
        }

        String password = registerRequestDTO.getPassword();
        String hashedPassword = passwordEncoder.encode(password);
        registerRequestDTO.setPassword(hashedPassword);
        return userService.createUser(registerRequestDTO);
    }

    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        boolean isEmailExist = userService.isUserExistByEmail(resetPasswordRequestDTO.getEmail());
        if (isEmailExist) {
            throw new DuplicateDataException("User with email " + resetPasswordRequestDTO.getEmail() + " is already exist.");
        }

        String password = resetPasswordRequestDTO.getNewPassword();
        String hashedPassword = passwordEncoder.encode(password);
        resetPasswordRequestDTO.setNewPassword(hashedPassword);
        userService.updateUserPassword(resetPasswordRequestDTO);
    }
}
