package com.github.harisherdiansyah.seapediaapi.features.users;

import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.features.authentication.RegisterRequestDTO;
import com.github.harisherdiansyah.seapediaapi.features.authentication.RegisterResponseDTO;
import com.github.harisherdiansyah.seapediaapi.features.authentication.ResetPasswordRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
}
