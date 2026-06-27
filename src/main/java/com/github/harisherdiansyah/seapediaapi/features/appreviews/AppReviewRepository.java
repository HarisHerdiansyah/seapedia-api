package com.github.harisherdiansyah.seapediaapi.features.appreviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface AppReviewRepository extends JpaRepository<AppReviewEntity, UUID> {
    @Query("SELECT new com.github.harisherdiansyah.seapediaapi.features.appreviews.AppReviewsResponseDTO(a.id, a.reviewer, a.content, a.rating) FROM AppReviewEntity a " +
    "WHERE a.rating >= 4.0 ORDER BY a.createdAt DESC")
    List<AppReviewsResponseDTO> findAllProjectionsBy();
}
