package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Address", description = "User Shipping Address Management")
public class UserAddressController {
    private final UserAddressService userAddressService;

    @Operation(summary = "Get all user addresses", description = "Retrieves all shipping addresses belonging to the authenticated buyer. Requires BUYER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User addresses retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("")
    public ResponseEntity<?> getAllUserAddresses() {
        List<UserAddressResponseDTO> data = userAddressService.getAllUserAddresses();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User addresses retrieved successfully.", data));
    }

    @Operation(summary = "Get user address by ID", description = "Retrieves a specific shipping address by its UUID. Requires BUYER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User address retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - address does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserAddressById(
            @Parameter(description = "Address UUID") @PathVariable("id") UUID addressId) {
        UserAddressResponseDTO data = userAddressService.getUserAddressById(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User address retrieved successfully.", data));
    }

    @Operation(summary = "Add user address", description = "Creates a new shipping address for the authenticated buyer. Requires BUYER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User address created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("")
    public ResponseEntity<?> addUserAddress(@Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO) {
        UserAddressResponseDTO data = userAddressService.addUserAddress(userAddressRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "User address created successfully.", data));
    }

    @Operation(summary = "Update user address", description = "Updates an existing shipping address by UUID. Requires BUYER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User address updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - address does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserAddress(
            @Parameter(description = "Address UUID") @PathVariable("id") UUID addressId,
            @Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO
    ) {
        UserAddressResponseDTO data = userAddressService.updateUserAddress(addressId, userAddressRequestDTO);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User address updated successfully.", data));
    }

    @Operation(summary = "Delete user address", description = "Deletes a shipping address by UUID. Requires BUYER role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - token missing or invalid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - requires BUYER role"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not found - address does not exist"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserAddress(
            @Parameter(description = "Address UUID") @PathVariable("id") UUID addressId) {
        userAddressService.deleteUserAddress(addressId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User address deleted successfully.", null));
    }
}
