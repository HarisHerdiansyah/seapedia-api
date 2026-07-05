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
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreEntity;
import com.github.harisherdiansyah.seapediaapi.features.stores.StoreRepository;
import com.github.harisherdiansyah.seapediaapi.features.wallets.WalletEntity;
import com.github.harisherdiansyah.seapediaapi.features.wallets.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DriverRepository driverRepository;
    private final WalletRepository walletRepository;
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

    @Transactional
    public RegisterResponseDTO createUser(RegisterRequestDTO requestDTO) {
        UserEntity.UserEntityBuilder builder = UserEntity.builder()
                .username(requestDTO.getUsername())
                .email(requestDTO.getEmail())
                .passwordHash(requestDTO.getPassword())
                .role(requestDTO.getRole());

        UserEntity newUser = userRepository.save(builder.build());
        WalletEntity newWallet = WalletEntity.builder()
                .user(newUser)
                .balance(BigDecimal.ZERO)
                .build();

        walletRepository.save(newWallet);

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

        Optional<UserEntity> user = userRepository.findUserEntityById(userId);
        boolean isUserExist = user.isPresent();
        if (!isUserExist) throw new NotFoundException("User with id " + userId + " is never exist.");
        allowedAs.add(ActiveRole.BUYER);

        Optional<StoreEntity> store = storeRepository.findByUserId(userId);
        boolean isStoreExist = store.isPresent();
        if (isStoreExist) allowedAs.add(ActiveRole.SELLER);

        boolean isDriver = driverRepository.existsByUserId(userId);
        if (isDriver) allowedAs.add(ActiveRole.DRIVER);

        WalletEntity userWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Wallet for user with id " + userId + " is never exist."));

        SessionEntity session = sessionService.getUserSessionInfoByUserId(userId);
        return new ProfileSummaryResponseDTO(
                user.map(UserEntity::getId).orElse(null),
                user.map(UserEntity::getUsername).orElse(null),
                user.map(UserEntity::getEmail).orElse(null),
                session.getActiveRole(),
                allowedAs,
                userWallet.getId(),
                userWallet.getBalance(),
                store.map(StoreEntity::getId).orElse(null),
                store.map(StoreEntity::getStoreName).orElse(null),
                store.map(StoreEntity::getLocation).orElse(null)
        );
    }
}
