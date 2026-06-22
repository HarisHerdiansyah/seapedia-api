package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import com.github.harisherdiansyah.seapediaapi.core.utils.RequestUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RequestUtility requestUtility;

    @PostMapping("/register")
    public ResponseEntity<?> registerController(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        RegisterResponseDTO responseDTO = authenticationService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Register success.", responseDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginController(@Valid @RequestBody LoginRequestDTO requestDTO, HttpServletRequest httpServletRequest) {
        String ipAddress = requestUtility.getClientIpAddress(httpServletRequest);
        String deviceInfo = requestUtility.getClientDeviceInfo(httpServletRequest);

        Map<String, Object> result = authenticationService.login(requestDTO, ipAddress, deviceInfo);
        LoginResponseDTO loginResponse = (LoginResponseDTO) result.get("loginResponse");
        String cookieResponse = (String) result.get("cookieResponse");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookieResponse)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Login successful.", loginResponse));
    }

    @PostMapping("/non-admin-role")
    public ResponseEntity<?> nonAdminRoleController(
            @Valid @RequestBody NonAdminRoleRequestDTO nonAdminRoleRequestDTO,
            @CookieValue(name = "refreshToken", defaultValue = "") String rt) {
        authenticationService.selectActiveRole(nonAdminRoleRequestDTO, rt);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Active role updated successfully.", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenController(
            @CookieValue(name = "refreshToken", defaultValue = "") String rt, HttpServletRequest httpServletRequest) {
        String ipAddress = requestUtility.getClientIpAddress(httpServletRequest);
        String deviceInfo = requestUtility.getClientDeviceInfo(httpServletRequest);

        Map<String, Object> result = authenticationService.refreshToken(rt, ipAddress, deviceInfo);
        LoginResponseDTO loginResponse = (LoginResponseDTO) result.get("loginResponse");
        String cookieResponse = (String) result.get("cookieResponse");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookieResponse)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed successfully.", loginResponse));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPasswordController(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        authenticationService.resetPassword(requestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Password reset successful.", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutController(@CookieValue(name = "refreshToken", defaultValue = "") String rt) {
        String response = authenticationService.logout(rt);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Logout successful.", null));
    }
}
