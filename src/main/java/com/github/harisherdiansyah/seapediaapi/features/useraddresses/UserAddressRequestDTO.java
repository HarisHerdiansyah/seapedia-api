package com.github.harisherdiansyah.seapediaapi.features.useraddresses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddressRequestDTO {
    @NotBlank(message = "Address name is required")
    @Size(min = 10, max = 100, message = "Address name must be between 10 and 100 characters")
    private String addressName;

    @NotBlank(message = "Receiver name is required")
    @Size(min = 10, max = 100, message = "Receiver name must be between 10 and 100 characters")
    private String receiverName;

    @NotBlank(message = "Receiver phone number is required")
    @Size(min = 10, max = 15, message = "Receiver phone number must be between 10 and 15 characters")
    private String receiverPhone;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "Determine if this address is default or not")
    private Boolean isDefault;
}
