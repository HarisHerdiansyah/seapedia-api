package com.github.harisherdiansyah.seapediaapi.features.delivery;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @RequestMapping("/populate")
    public ResponseEntity<?> populateDeliveryMethods() {
        List<DeliveryMethodResponseDTO> data = deliveryService.populateDeliveryMethods();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Delivery methods retrieved successfully.", data));
    }
}
