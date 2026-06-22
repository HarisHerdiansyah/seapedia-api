package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerController(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        RegisterResponseDTO responseDTO = authenticationService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Register success.", responseDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginController(@Valid @RequestBody LoginRequestDTO requestDTO) {
        return null;
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPasswordController(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        authenticationService.resetPassword(requestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Reset password success.", null));
    }
}
