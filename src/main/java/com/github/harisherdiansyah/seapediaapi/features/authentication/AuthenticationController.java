package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import com.github.harisherdiansyah.seapediaapi.core.utils.RequestUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and User Session Management")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RequestUtility requestUtility;

    @Operation(summary = "Register new user", description = "Registers a new user to the system.")
    @PostMapping("/register")
    public ResponseEntity<?> registerController(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        RegisterResponseDTO responseDTO = authenticationService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Register success.", responseDTO));
    }

    @Operation(summary = "Login", description = "Login to the system and obtain access token and refresh token via cookie.")
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

    @Operation(summary = "Select active role", description = "Selects a role (Buyer/Seller) for non-admin users.")
    @PreAuthorize("hasAuthority('SELECT_ROLE') and hasRole('NON_ADMIN')")
    @PostMapping("/select-active-role")
    public ResponseEntity<?> selectActiveRoleController(
            @Valid @RequestBody NonAdminRoleRequestDTO nonAdminRoleRequestDTO,
            @Parameter(description = "Refresh Token from Cookie") @CookieValue(name = "refreshToken", defaultValue = "") String rt) {
        NonAdminRoleResponseDTO responseDTO =  authenticationService.selectActiveRole(nonAdminRoleRequestDTO, rt);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Active role updated successfully.", responseDTO));
    }

    @Operation(summary = "Refresh token", description = "Renews access token using a valid refresh token.")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshTokenController(
            @Parameter(description = "Refresh Token from Cookie") @CookieValue(name = "refreshToken", defaultValue = "") String rt, HttpServletRequest httpServletRequest) {
        String ipAddress = requestUtility.getClientIpAddress(httpServletRequest);
        String deviceInfo = requestUtility.getClientDeviceInfo(httpServletRequest);

        Map<String, Object> result = authenticationService.refreshToken(rt, ipAddress, deviceInfo);
        LoginResponseDTO loginResponse = (LoginResponseDTO) result.get("loginResponse");
        String cookieResponse = (String) result.get("cookieResponse");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookieResponse)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed successfully.", loginResponse));
    }

    @Operation(summary = "Reset password", description = "Changes user password.")
    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPasswordController(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        authenticationService.resetPassword(requestDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Password reset successful.", null));
    }

    @Operation(summary = "Logout", description = "Logs out from the system and removes the refresh token.")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutController(@Parameter(description = "Refresh Token from Cookie") @CookieValue(name = "refreshToken", defaultValue = "") String rt) {
        String response = authenticationService.logout(rt);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Logout successful.", null));
    }
}
