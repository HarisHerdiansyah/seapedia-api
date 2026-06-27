package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app-review")
@RequiredArgsConstructor
public class AppReviewController {
    private final AppReviewService appReviewService;

    @GetMapping("")
    public ResponseEntity<?> getAppReviews() {
        List<AppReviewsResponseDTO> response = appReviewService.getAppReviews();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "App reviews retrieved successfully.", response));
    }

    @PostMapping("")
    public ResponseEntity<?> submitAppReview(@Valid @RequestBody AppReviewRequestDTO appReviewRequestDTO) {
        appReviewService.submitAppReview(appReviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Review submitted successfully.", null));
    }
}
