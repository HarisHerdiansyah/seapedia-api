package com.github.harisherdiansyah.seapediaapi.features.stores;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PreAuthorize("hasRole('NON_ADMIN') and hasAuthority('REGISTER_SELLLER')")
    @PostMapping("")
    public ResponseEntity<?> registerStore(@Valid @RequestBody StoreRegisterRequestDTO storeRegisterRequestDTO) {
        storeService.registerStore(storeRegisterRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Store registered.", null));
    }
}
