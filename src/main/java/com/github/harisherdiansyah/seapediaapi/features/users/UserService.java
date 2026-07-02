package com.github.harisherdiansyah.seapediaapi.features.users;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.authentication.RegisterRequestDTO;
import com.github.harisherdiansyah.seapediaapi.features.authentication.RegisterResponseDTO;
import com.github.harisherdiansyah.seapediaapi.features.authentication.ResetPasswordRequestDTO;
import com.github.harisherdiansyah.seapediaapi.features.drivers.DriverRepository;
import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import com.github.harisherdiansyah.seapediaapi.features.session.SessionEntity;
import com.github.harisherdiansyah.seapediaapi.features.session.SessionService;
import com.github.harisherdiansyah.seapediaapi.features.session.UserSessionInfo;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreEntity;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DriverRepository driverRepository;
    private final SessionService sessionService;

    public boolean isUserExistByUsername(String username) {
        return userRepository.existsUserEntityByUsername(username);
    }

    public boolean isUserExistByEmail(String email) {
        return userRepository.existsUserEntityByEmail(email);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " is never exist."));
    }

    public UserEntity getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " is never exist."));
    }

    public RegisterResponseDTO createUser(RegisterRequestDTO requestDTO) {
        UserEntity.UserEntityBuilder builder = UserEntity.builder()
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .passwordHash(requestDTO.getPassword())
                .role(requestDTO.getRole());

        UserEntity newUser = userRepository.save(builder.build());
        return new RegisterResponseDTO(newUser.getId(), newUser.getUsername(), newUser.getEmail(), newUser.getRole());
    }

    public void updateUserPassword(ResetPasswordRequestDTO requestDTO) {
        UserEntity user = userRepository.findUserEntityByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User with email " + requestDTO.getEmail() + " is never exist."));

        user.setPasswordHash(requestDTO.getNewPassword());
        userRepository.save(user);
    }

    public ProfileSummaryResponseDTO getProfileSummary() {
        UUID userId = SecurityUtil.getCurrentUserId();
        List<ActiveRole> allowedAs = new ArrayList<>();

        UserEntity user = userRepository.findUserEntityById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        allowedAs.add(ActiveRole.BUYER);

        StoreEntity store = storeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User is not register their store yet."));
        if (store != null) allowedAs.add(ActiveRole.SELLER);

        boolean isDriver = driverRepository.existsByUserId(userId);
        if (isDriver) allowedAs.add(ActiveRole.DRIVER);

        SessionEntity session = sessionService.getUserSessionInfoByUserId(userId);
        return new ProfileSummaryResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                session.getActiveRole(),
                allowedAs,
                store != null ? store.getId() : null,
                store != null ? store.getStoreName() : null,
                store != null ? store.getLocation() : null
        );
    }
}
