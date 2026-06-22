package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.exception.DuplicateDataException;
import com.github.harisherdiansyah.seapediaapi.core.exception.ForbiddenException;
import com.github.harisherdiansyah.seapediaapi.core.utils.JwtUtility;
import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import com.github.harisherdiansyah.seapediaapi.features.session.CreateSessionDTO;
import com.github.harisherdiansyah.seapediaapi.features.session.SessionService;
import com.github.harisherdiansyah.seapediaapi.features.users.UserRole;
import com.github.harisherdiansyah.seapediaapi.features.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;

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

    public Map<String, Object> login(LoginRequestDTO loginRequestDTO, String ipAddress, String deviceInfo) {
        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        UserPrincipalEntity principal = (UserPrincipalEntity) auth.getPrincipal();
        return sessionManager(principal, ipAddress, deviceInfo);
    }

    public void selectActiveRole(NonAdminRoleRequestDTO nonAdminRoleRequestDTO, String rt) {
        UUID rtJti = UUID.fromString(jwtUtility.extractJti(rt));
        sessionService.updateActiveRoleSession(rtJti, nonAdminRoleRequestDTO.getActiveRole());
    }

    public Map<String, Object> refreshToken(String rt, String ipAddress, String deviceInfo) {
        if (!StringUtils.hasText(rt)) {
            throw new ForbiddenException("Refresh token empty. Rotation isn't allowed.");
        }

        UUID rtJti = UUID.fromString(jwtUtility.extractJti(rt));
        if (!sessionService.isSessionExist(rtJti)) {
            throw new ForbiddenException("Session isn't exist. Try to re-login.");
        }

        sessionService.deleteSession(rtJti);

        Authentication auth = jwtUtility.getAuthentication(rt);
        UserPrincipalEntity principal = (UserPrincipalEntity) auth.getPrincipal();
        return sessionManager(principal, ipAddress, deviceInfo);
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

    private Map<String, Object> sessionManager(UserPrincipalEntity principal, String ipAddress, String deviceInfo) {
        List<String> authorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("userId", principal.getUserId());
        tokenClaims.put("userRole", authorities);

        String at = jwtUtility.generateAt(tokenClaims, principal);
        String rt = jwtUtility.generateRt(tokenClaims, principal);

        UUID rtJti = UUID.fromString(jwtUtility.extractJti(rt));
        CreateSessionDTO sessionDTO = new CreateSessionDTO(rtJti, principal.getUserId(), deviceInfo, ipAddress, ActiveRole.NON_ADMIN);
        sessionService.createSession(sessionDTO);

        String cookieResponse = jwtUtility.rtResponseBuilder(rt).toString();

        UserRole role = authorities.stream().map(UserRole::valueOf).findFirst().orElse(UserRole.NON_ADMIN);
        LoginResponseDTO.UserObject userObject = new LoginResponseDTO.UserObject(
                principal.getUserId(), "", principal.getUsername(), role
        );

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(at, userObject);

        Map<String, Object> result = new HashMap<>();
        result.put("loginResponse", loginResponseDTO);
        result.put("cookieResponse", cookieResponse);
        return result;
    }
}
