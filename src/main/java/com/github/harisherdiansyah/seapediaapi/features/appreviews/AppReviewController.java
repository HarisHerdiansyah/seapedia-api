package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app-review")
@RequiredArgsConstructor
@Tag(name = "App Review", description = "App Review Management")
public class AppReviewController {
    private final AppReviewService appReviewService;

    @Operation(summary = "Get app reviews", description = "Retrieves a list of app reviews from users.")
    @GetMapping("")
    public ResponseEntity<?> getAppReviews() {
        List<AppReviewsResponseDTO> response = appReviewService.getAppReviews();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "App reviews retrieved successfully.", response));
    }

    @Operation(summary = "Submit app review", description = "Submits a new review for the application.")
    @PostMapping("")
    public ResponseEntity<?> submitAppReview(@Valid @RequestBody AppReviewRequestDTO appReviewRequestDTO) {
        appReviewService.submitAppReview(appReviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Review submitted successfully.", null));
    }
}
