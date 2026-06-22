package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.exception.DuplicateDataException;
import com.github.harisherdiansyah.seapediaapi.core.exception.ForbiddenException;
import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.JwtUtility;
import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import com.github.harisherdiansyah.seapediaapi.features.session.CreateSessionDTO;
import com.github.harisherdiansyah.seapediaapi.features.session.SessionService;
import com.github.harisherdiansyah.seapediaapi.features.users.UserRole;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;

    /** Active roles that NON_ADMIN users are permitted to select. */
    private static final Set<ActiveRole> NON_ADMIN_ALLOWED_ROLES = Set.of(
            ActiveRole.BUYER, ActiveRole.SELLER, ActiveRole.DRIVER
    );

    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        boolean isUserExist = userService.isUserExistByUsername(registerRequestDTO.getUsername());
        if (isUserExist) {
            throw new DuplicateDataException("User with username " + registerRequestDTO.getUsername() + " already exists.");
        }

        boolean isEmailExist = userService.isUserExistByEmail(registerRequestDTO.getEmail());
        if (isEmailExist) {
            throw new DuplicateDataException("User with email " + registerRequestDTO.getEmail() + " already exists.");
        }

        String hashedPassword = passwordEncoder.encode(registerRequestDTO.getPassword());
        registerRequestDTO.setPassword(hashedPassword);
        return userService.createUser(registerRequestDTO);
    }

    public Map<String, Object> login(LoginRequestDTO loginRequestDTO, String ipAddress, String deviceInfo) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        UserPrincipalEntity principal = (UserPrincipalEntity) auth.getPrincipal();
        return buildSessionAndTokens(principal, ipAddress, deviceInfo);
    }

    public void selectActiveRole(NonAdminRoleRequestDTO nonAdminRoleRequestDTO, String rt) {
        UUID rtJti = UUID.fromString(jwtUtility.extractJti(rt));

        // Determine user's base role from the refresh token claims
        List<String> roles = jwtUtility.extractRoles(rt);
        UserRole userRole = roles.stream()
                .map(UserRole::valueOf)
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("Unable to determine user role from token."));

        ActiveRole requestedRole = nonAdminRoleRequestDTO.getActiveRole();

        if (userRole == UserRole.ADMIN) {
            // ADMIN can only re-select ADMIN
            if (requestedRole != ActiveRole.ADMIN) {
                throw new ForbiddenException("Admin users can only select the ADMIN role.");
            }
        } else {
            // NON_ADMIN can only select BUYER, SELLER, or DRIVER
            if (!NON_ADMIN_ALLOWED_ROLES.contains(requestedRole)) {
                throw new ForbiddenException("Non-admin users can only select BUYER, SELLER, or DRIVER as their active role.");
            }
        }

        sessionService.updateActiveRoleSession(rtJti, requestedRole);
    }

    public Map<String, Object> refreshToken(String rt, String ipAddress, String deviceInfo) {
        if (!StringUtils.hasText(rt)) {
            throw new ForbiddenException("Refresh token is missing.");
        }

        UUID rtJti = UUID.fromString(jwtUtility.extractJti(rt));
        if (!sessionService.isSessionExist(rtJti)) {
            throw new ForbiddenException("Session not found. Please re-login.");
        }

        sessionService.deleteSession(rtJti);

        Authentication auth = jwtUtility.getAuthentication(rt);
        UserPrincipalEntity principal = (UserPrincipalEntity) auth.getPrincipal();
        return buildSessionAndTokens(principal, ipAddress, deviceInfo);
    }

    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        boolean isEmailExist = userService.isUserExistByEmail(resetPasswordRequestDTO.getEmail());
        if (!isEmailExist) {
            throw new NotFoundException("User with email " + resetPasswordRequestDTO.getEmail() + " not found.");
        }

        String hashedPassword = passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword());
        resetPasswordRequestDTO.setNewPassword(hashedPassword);
        userService.updateUserPassword(resetPasswordRequestDTO);
    }

    public String logout(String rt) {
        String clearedCookie = jwtUtility.buildRefreshTokenCookie("").toString();
        if (!StringUtils.hasText(rt)) return clearedCookie;

        UUID rtJti = UUID.fromString(jwtUtility.extractJti(rt));
        if (!sessionService.isSessionExist(rtJti)) return clearedCookie;

        sessionService.deleteSession(rtJti);
        return clearedCookie;
    }

    private Map<String, Object> buildSessionAndTokens(UserPrincipalEntity principal, String ipAddress, String deviceInfo) {
        List<String> authorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("userId", principal.getUserId());
        tokenClaims.put("roles", authorities);  // key must match JwtUtility.extractRoles()

        String accessToken = jwtUtility.generateAccessToken(tokenClaims, principal);
        String refreshToken = jwtUtility.generateRefreshToken(tokenClaims, principal);

        UUID rtJti = UUID.fromString(jwtUtility.extractJti(refreshToken));
        CreateSessionDTO sessionDTO = new CreateSessionDTO(rtJti, principal.getUserId(), deviceInfo, ipAddress, ActiveRole.NON_ADMIN);
        sessionService.createSession(sessionDTO);

        String cookieResponse = jwtUtility.buildRefreshTokenCookie(refreshToken).toString();

        UserRole role = authorities.stream().map(UserRole::valueOf).findFirst().orElse(UserRole.NON_ADMIN);
        LoginResponseDTO.UserObject userObject = new LoginResponseDTO.UserObject(
                principal.getUserId(), principal.getDisplayName(), principal.getUsername(), role
        );

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(accessToken, userObject);

        Map<String, Object> result = new HashMap<>();
        result.put("loginResponse", loginResponseDTO);
        result.put("cookieResponse", cookieResponse);
        return result;
    }
}
