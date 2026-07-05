package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import com.github.harisherdiansyah.seapediaapi.core.exception.ForbiddenException;
import com.github.harisherdiansyah.seapediaapi.core.exception.NotFoundException;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityUtil;
import com.github.harisherdiansyah.seapediaapi.features.users.UserEntity;
import com.github.harisherdiansyah.seapediaapi.features.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    public List<UserAddressResponseDTO> getAllUserAddresses() {
        UUID userId = SecurityUtil.getCurrentUserId();

        return userAddressRepository.findAllByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UserAddressResponseDTO getUserAddressById(UUID addressId) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UserAddressEntity address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + addressId));

        return toResponseDTO(address);
    }

    @Transactional
    public UserAddressResponseDTO addUserAddress(UserAddressRequestDTO dto) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UserEntity user = userRepository.findUserEntityById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        boolean userHasNoAddress = !userAddressRepository.existsByUserId(userId);

        boolean isDefault;
        if (userHasNoAddress) {
            isDefault = true;
        } else if (Boolean.TRUE.equals(dto.getIsDefault())) {
            userAddressRepository.resetDefaultByUserId(userId);
            isDefault = true;
        } else {
            isDefault = false;
        }

        UserAddressEntity newAddress = UserAddressEntity.builder()
                .user(user)
                .addressName(dto.getAddressName())
                .receiverName(dto.getReceiverName())
                .receiverPhone(dto.getReceiverPhone())
                .district(dto.getDistrict())
                .province(dto.getProvince())
                .city(dto.getCity())
                .streetAddress(dto.getStreetAddress())
                .postalCode(dto.getPostalCode())
                .isDefault(isDefault)
                .build();

        UserAddressEntity saved = userAddressRepository.save(newAddress);
        return toResponseDTO(saved);
    }

    @Transactional
    public UserAddressResponseDTO updateUserAddress(UUID addressId, UserAddressRequestDTO dto) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UserAddressEntity address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + addressId));

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            userAddressRepository.resetDefaultByUserId(userId);
            address.setIsDefault(true);
        } else {
            address.setIsDefault(false);
        }

        address.setAddressName(dto.getAddressName());
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setDistrict(dto.getDistrict());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setStreetAddress(dto.getStreetAddress());
        address.setPostalCode(dto.getPostalCode());

        UserAddressEntity saved = userAddressRepository.save(address);
        return toResponseDTO(saved);
    }

    @Transactional
    public void deleteUserAddress(UUID addressId) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UserAddressEntity address = userAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + addressId));

        long totalAddresses = userAddressRepository.countByUserId(userId);
        if (totalAddresses <= 1) {
            throw new ForbiddenException("Cannot delete the only remaining address.");
        }

        userAddressRepository.delete(address);
    }

    private UserAddressResponseDTO toResponseDTO(UserAddressEntity entity) {
        return new UserAddressResponseDTO(
                entity.getUser().getId(),
                entity.getId(),
                entity.getAddressName(),
                entity.getReceiverName(),
                entity.getReceiverPhone(),
                entity.getDistrict(),
                entity.getProvince(),
                entity.getCity(),
                entity.getStreetAddress(),
                entity.getPostalCode(),
                entity.getIsDefault()
        );
    }
}
