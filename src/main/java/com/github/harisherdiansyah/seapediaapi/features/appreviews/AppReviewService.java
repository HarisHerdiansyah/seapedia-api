package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppReviewService {
    private final AppReviewRepository appReviewRepository;

    public void submitAppReview(AppReviewRequestDTO appReviewRequestDTO) {
        AppReviewEntity builder = AppReviewEntity.builder()
                .reviewer(appReviewRequestDTO.getReviewer())
                .rating(appReviewRequestDTO.getRating())
                .content(appReviewRequestDTO.getContent())
                .build();

        appReviewRepository.save(builder);
    }
}
