package com.github.harisherdiansyah.seapediaapi.features.healthcheck;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-check")
public class HealthCheckController {
    @GetMapping("")
    public ResponseEntity<?> getApiHealth() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "Status Up", null));
    }
}
