package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AppReviewService {
    private final AppReviewRepository appReviewRepository;

    public List<AppReviewsResponseDTO> getAppReviews() {
        return appReviewRepository.findAllProjectionsBy();
    }

    public void submitAppReview(AppReviewRequestDTO appReviewRequestDTO) {
        boolean isAnon = appReviewRequestDTO.getReviewer() == null || appReviewRequestDTO.getReviewer().isEmpty();

        AppReviewEntity builder = AppReviewEntity.builder()
                .reviewer(isAnon ? generateReviewerName() : appReviewRequestDTO.getReviewer())
                .rating(appReviewRequestDTO.getRating())
                .content(appReviewRequestDTO.getContent())
                .build();

        appReviewRepository.save(builder);
    }

    private String generateReviewerName() {
        int randomNum = ThreadLocalRandom.current().nextInt(1000000, 10000000);
        return "anon" + randomNum;
    }
}
