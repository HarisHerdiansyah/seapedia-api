package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppReviewRepository extends JpaRepository<AppReviewEntity, UUID> {
}
