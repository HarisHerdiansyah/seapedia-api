package com.github.harisherdiansyah.seapediaapi.features.drivers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;

    public boolean isDriverExistByUserId(UUID userId) {
        return driverRepository.existsByUser_Id(userId);
    }
}
