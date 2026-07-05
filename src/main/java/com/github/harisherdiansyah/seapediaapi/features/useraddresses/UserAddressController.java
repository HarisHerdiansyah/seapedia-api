package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("")
    public ResponseEntity<?> getAllUserAddresses() {
        return null;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserAddressById(@PathVariable UUID addressId) {
        return null;
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("")
    public ResponseEntity<?> addUserAddress(@Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO) {
        return null;
    }

    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO
    ) {
        return null;
    }

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserAddress(@PathVariable UUID addressId) {
        return null;
    }
}
