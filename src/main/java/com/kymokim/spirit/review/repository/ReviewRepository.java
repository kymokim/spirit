package com.kymokim.spirit.review.repository;

import com.kymokim.spirit.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByStoreIdOrderByHistoryInfo_CreatedAtDesc(Long storeId, Pageable pageable);
    Page<Review> findAllByWriterIdOrderByHistoryInfo_CreatedAtDesc(Long writerId, Pageable pageable);
}
