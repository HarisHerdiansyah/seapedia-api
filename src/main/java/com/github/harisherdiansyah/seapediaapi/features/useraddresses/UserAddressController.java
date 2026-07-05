package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("")
    public ResponseEntity<?> getAllUserAddresses() {
        List<UserAddressResponseDTO> data = userAddressService.getAllUserAddresses();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User addresses retrieved successfully.", data));
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserAddressById(@PathVariable("id") UUID addressId) {
        UserAddressResponseDTO data = userAddressService.getUserAddressById(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User address retrieved successfully.", data));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("")
    public ResponseEntity<?> addUserAddress(@Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO) {
        UserAddressResponseDTO data = userAddressService.addUserAddress(userAddressRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "User address created successfully.", data));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserAddress(
            @PathVariable("id") UUID addressId,
            @Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO
    ) {
        UserAddressResponseDTO data = userAddressService.updateUserAddress(addressId, userAddressRequestDTO);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User address updated successfully.", data));
    }

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserAddress(@PathVariable("id") UUID addressId) {
        userAddressService.deleteUserAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User address deleted successfully.", null));
    }
}
