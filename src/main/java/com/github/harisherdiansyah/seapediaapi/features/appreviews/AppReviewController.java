package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import com.github.harisherdiansyah.seapediaapi.core.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-review")
@RequiredArgsConstructor
public class AppReviewController {
    private final AppReviewService appReviewService;

    @PostMapping("")
    public ResponseEntity<?> submitAppReview(@Valid @RequestBody AppReviewRequestDTO appReviewRequestDTO) {
        appReviewService.submitAppReview(appReviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Review submitted successfully..", null));
    }
}
