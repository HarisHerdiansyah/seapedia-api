package com.github.harisherdiansyah.seapediaapi.features.authentication;

import com.github.harisherdiansyah.seapediaapi.core.exception.DuplicateDataException;
import com.github.harisherdiansyah.seapediaapi.core.exception.ForbiddenException;
import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.JwtUtility;
import com.github.harisherdiansyah.seapediaapi.features.drivers.DriverService;
import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import com.github.harisherdiansyah.seapediaapi.features.session.CreateSessionDTO;
import com.github.harisherdiansyah.seapediaapi.features.session.SessionService;
import com.github.harisherdiansyah.seapediaapi.features.session.UserSessionInfo;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreService;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final SessionService sessionService;
    private final StoreService storeService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final AuthenticationManager authenticationManager;

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
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getEmail(), loginRequestDTO.getPassword()
        );
        Authentication auth = authenticationManager.authenticate(token);
        UserPrincipalEntity principal = (UserPrincipalEntity) auth.getPrincipal();
        return buildSessionAndTokens(principal, ipAddress, deviceInfo);
    }

    public NonAdminRoleResponseDTO selectActiveRole(NonAdminRoleRequestDTO nonAdminRoleRequestDTO, String rt) {
        if (!StringUtils.hasText(rt)) {
            throw new ForbiddenException("Refresh token is missing.");
        }

        UUID currentTokenJti = UUID.fromString(jwtUtility.extractJti(rt));
        ActiveRole requestedRole = nonAdminRoleRequestDTO.getActiveRole();

        UserSessionInfo userSessionInfo = sessionService.getUserSessionInfo(currentTokenJti);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userSessionInfo.getId());
        claims.put("role", requestedRole.toString());

        String accessToken = jwtUtility.generateAccessToken(claims, currentTokenJti.toString(), userSessionInfo.getEmail());

        sessionService.updateActiveRoleSession(currentTokenJti, requestedRole);
        return new NonAdminRoleResponseDTO(requestedRole, accessToken);
    }

    public Map<String, Object> refreshToken(String rt, String ipAddress, String deviceInfo) {
        if (!StringUtils.hasText(rt)) {
            throw new ForbiddenException("Refresh token is missing.");
        }

        UUID currentTokenJti = UUID.fromString(jwtUtility.extractJti(rt));
        if (!sessionService.isSessionExist(currentTokenJti)) {
            throw new ForbiddenException("Session not found. Please re-login.");
        }

        UserSessionInfo userSessionInfo = sessionService.getUserSessionInfo(currentTokenJti);
        sessionService.deleteSession(currentTokenJti);

        UUID userId = userSessionInfo.getId();
        String activeRoleStr = String.valueOf(userSessionInfo.getActiveRole());
        String email = userSessionInfo.getEmail();
        String role = String.valueOf(userSessionInfo.getRole());

        ActiveRole activeRole = ActiveRole.valueOf(activeRoleStr);
        List<GrantedAuthority> authorities = activeRole.getAuthorities();

        Authentication authToken = jwtUtility.buildAuthToken(userId, email, role, authorities);
        UserPrincipalEntity principal = (UserPrincipalEntity) authToken.getPrincipal();
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

    private Map<String, Object> buildAuthResponse(UserPrincipalEntity principal, String at, String rt) {
        List<ActiveRole> allowedAs = new ArrayList<>();

        boolean isSeller = storeService.isStoreExistByUserId(principal.getUserId());
        boolean isDriver = driverService.isDriverExistByUserId(principal.getUserId());
        if (isSeller) allowedAs.add(ActiveRole.SELLER);
        if (isDriver) allowedAs.add(ActiveRole.DRIVER);
        allowedAs.add(ActiveRole.BUYER);

        String cookieResponse = jwtUtility.buildRefreshTokenCookie(rt).toString();
        LoginResponseDTO.UserObject userObject = new LoginResponseDTO.UserObject(
                principal.getUserId(),
                principal.getDisplayName(),
                principal.getUsername(),
                UserRole.valueOf(principal.getUserRole()),
                allowedAs
        );

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(at, userObject);

        Map<String, Object> result = new HashMap<>();
        result.put("loginResponse", loginResponseDTO);
        result.put("cookieResponse", cookieResponse);
        return result;
    }

    private Map<String, Object> buildSessionAndTokens(UserPrincipalEntity principal, String ipAddress, String deviceInfo) {
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("userId", principal.getUserId());
        tokenClaims.put("role", principal.getUserRole());

        UUID tokenJti = UUID.randomUUID();
        String tokenJtiString = tokenJti.toString();
        String tokenSubject = principal.getUsername();
        String accessToken = jwtUtility.generateAccessToken(tokenClaims, tokenJtiString, tokenSubject);
        String refreshToken = jwtUtility.generateRefreshToken(tokenJtiString, tokenSubject);

        boolean isAdmin = UserRole.ADMIN.toString().equals(principal.getUserRole());
        ActiveRole sessionActiveRole = isAdmin ? ActiveRole.ADMIN : ActiveRole.NON_ADMIN;

        CreateSessionDTO sessionDTO = new CreateSessionDTO(tokenJti, principal.getUserId(), deviceInfo, ipAddress, sessionActiveRole);
        sessionService.createSession(sessionDTO);

        return buildAuthResponse(principal, accessToken, refreshToken);
    }
}
