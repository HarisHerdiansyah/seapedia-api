package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import java.util.UUID;

public record UserAddressResponseDTO(
        UUID userId,
        UUID addressId,
        String addressName,
        String receiverName,
        String receiverPhone,
        String district,
        String province,
        String city,
        String streetAddress,
        String postalCode,
        Boolean isDefault
) {
}
