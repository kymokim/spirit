package com.kymokim.spirit.review.repository;

import com.kymokim.spirit.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByStoreIdOrderByHistoryInfo_CreatedAtDesc(Long storeId, Pageable pageable);
    Page<Review> findAllByWriterIdOrderByHistoryInfo_CreatedAtDesc(Long writerId, Pageable pageable);
    long countByWriterIdAndHistoryInfo_CreatedAtBetween(Long writerId, LocalDateTime startInclusive, LocalDateTime endInclusive);
    boolean existsByWriterIdAndStoreIdAndHistoryInfo_CreatedAtBetween(Long writerId, Long storeId, LocalDateTime startInclusive, LocalDateTime endInclusive);
}
